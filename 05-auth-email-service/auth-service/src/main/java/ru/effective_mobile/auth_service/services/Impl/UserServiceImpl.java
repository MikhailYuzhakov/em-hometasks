package ru.effective_mobile.auth_service.services.Impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective_mobile.auth_service.domain.User;
import ru.effective_mobile.auth_service.dto.response.PageResponse;
import ru.effective_mobile.auth_service.dto.UserCreateDto;
import ru.effective_mobile.auth_service.dto.UserDto;
import ru.effective_mobile.auth_service.exceptions.UserNotFoundException;
import ru.effective_mobile.auth_service.repository.UserRepository;
import ru.effective_mobile.auth_service.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    @Getter
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<UserDto> findAll(int page, int size) {
        long totalElements = count();

        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        int start = page * size;

        List<UserDto> userDtoList = userRepository.findUsersWithLimitAndOffset(size, start).stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());

        return new PageResponse<>(userDtoList, page, size, totalElements);
    }

    private int count() {
        return userRepository.countUsers();
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email '" + email + "' not found."));

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .code(user.getCode())
                .isVerified(user.isVerified())
                .build();
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public User saveUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(userCreateDto.getPassword());
        user.setCode(userCreateDto.getCode());
        user.setRole("ROLE_USER");
        user.setVerified(false);

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(UserDto userDto) {
        User updatedUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User ID: '" + userDto.getId() + "' not found"));

        updatedUser.setEmail(userDto.getEmail());
        updatedUser.setPassword(userDto.getPassword());
        updatedUser.setRole(userDto.getRole());
        updatedUser.setCode(userDto.getCode());
        return userRepository.save(updatedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() ->
                new UserNotFoundException("User with email '" + username + "' not found."));

    }
}
