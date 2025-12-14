package ru.effective_mobile.auth_service.services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effective_mobile.auth_service.domain.User;
import ru.effective_mobile.auth_service.dto.UserDto;
import ru.effective_mobile.auth_service.exceptions.UserNotFoundException;
import ru.effective_mobile.auth_service.repository.UserRepository;
import ru.effective_mobile.auth_service.services.UserService;

import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(UserDto userDto) {
        User updatedUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UserNotFoundException("User ID: '" + userDto.getId() + "' not found"));

        updatedUser.setEmail(userDto.getEmail());
        updatedUser.setPassword(userDto.getPassword());
        return userRepository.save(updatedUser);
    }
}
