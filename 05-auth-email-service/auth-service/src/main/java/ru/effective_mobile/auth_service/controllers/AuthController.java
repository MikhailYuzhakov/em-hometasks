package ru.effective_mobile.auth_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.effective_mobile.auth_service.dto.UserRegisteredEvent;
import ru.effective_mobile.auth_service.dto.response.JwtAuthenticationResponse;
import ru.effective_mobile.auth_service.dto.response.SignInRequest;
import ru.effective_mobile.auth_service.dto.response.SignUpRequest;
import ru.effective_mobile.auth_service.dto.response.SuccessResponse;
import ru.effective_mobile.auth_service.services.Impl.AuthenticationService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public SuccessResponse signUp(@RequestBody SignUpRequest request) {
        UserDetails userDetails = authenticationService.signUp(request);
        return new SuccessResponse("User '" + userDetails.getUsername() + "' is created. " +
                "Please verify your email by using POST /auth/verify");
    }

    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @PostMapping("/verify")
    public JwtAuthenticationResponse verifyEmail(@RequestBody UserRegisteredEvent request) {
        return authenticationService.verify(request);
    }
}
