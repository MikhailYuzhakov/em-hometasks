package ru.effective_mobile.auth_service.services;

import ru.effective_mobile.auth_service.domain.User;
import ru.effective_mobile.auth_service.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findUserByEmail(String email);
    void deleteUserById(Long id);
    User saveUser(UserDto userDto);
    User updateUser(UserDto userDto);
}
