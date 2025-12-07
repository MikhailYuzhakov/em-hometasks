package ru.effective_mobile.auth_service.services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.effective_mobile.auth_service.domain.User;
import ru.effective_mobile.auth_service.dto.*;
import ru.effective_mobile.auth_service.dto.response.JwtAuthenticationResponse;
import ru.effective_mobile.auth_service.dto.response.SignInRequest;
import ru.effective_mobile.auth_service.dto.response.SignUpRequest;
import ru.effective_mobile.auth_service.exceptions.VerificationCodeException;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public JwtAuthenticationResponse signIn(SignInRequest request) {

        UserDto user = userService.findUserByEmail(request.getEmail());
        if (!user.getIsVerified()) {
            throw new VerificationCodeException("Email '" + request.getEmail() + "' not verified. Please use POST /auth/verify to verify email.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        var jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }

    public UserDetails signUp(SignUpRequest request) {
        String verificationCode = String.valueOf(new Random().nextInt(9000) + 1000);
        log.info("verificationCode = {}", verificationCode);

        UserCreateDto userCreateDto = UserCreateDto.builder()
                        .email(request.getEmail())
                        .password(userService.getPasswordEncoder().encode(request.getPassword()))
                        .code(verificationCode)
                        .build();

        UserRegisteredEvent userRegisteredEvent = UserRegisteredEvent.builder()
                .email(request.getEmail())
                .verificationCode(verificationCode)
                .build();

        UserDetails newUser = userService.saveUser(userCreateDto);

        kafkaTemplate.send("user-registration-topic", userRegisteredEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("SUCCESS! Сообщение отправлено в топик: {}", "user-registration-topic");
                    } else {
                        log.error("ERROR! Не удалось отправить сообщение: {}", ex.getMessage(), ex);
                    }
                });

        return newUser;
    }

    public JwtAuthenticationResponse verify(UserRegisteredEvent request) {
        UserDto userDto = userService.findUserByEmail(request.getEmail());

        if (userDto.getCode().equals(request.getVerificationCode())) {
            userDto.setCode(null);
            userDto.setIsVerified(true);
            userService.updateUser(userDto);

            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());

            var jwt = jwtService.generateToken(user);

            return new JwtAuthenticationResponse(jwt);
        }
        throw new VerificationCodeException("Not valid verification code");
    }
}
