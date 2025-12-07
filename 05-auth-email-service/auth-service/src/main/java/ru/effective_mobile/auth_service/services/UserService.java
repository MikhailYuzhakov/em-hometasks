package ru.effective_mobile.auth_service.services;

import ru.effective_mobile.auth_service.domain.User;
import ru.effective_mobile.auth_service.dto.response.PageResponse;
import ru.effective_mobile.auth_service.dto.UserCreateDto;
import ru.effective_mobile.auth_service.dto.UserDto;

public interface UserService {
    PageResponse<UserDto> findAll(int limit, int offset);
    UserDto findUserByEmail(String email);
    void deleteUserById(Long id);
    User saveUser(UserCreateDto userCreateDto);
    User updateUser(UserDto userDto);
}
