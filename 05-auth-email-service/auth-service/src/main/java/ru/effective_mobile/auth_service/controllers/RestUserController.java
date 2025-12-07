package ru.effective_mobile.auth_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effective_mobile.auth_service.dto.response.PageResponse;
import ru.effective_mobile.auth_service.dto.UserDto;
import ru.effective_mobile.auth_service.services.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RestUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<PageResponse<UserDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<UserDto> userPage = userService.findAll(page, size);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDto> getUser(@PathVariable("email") String email) throws Exception {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }
}
