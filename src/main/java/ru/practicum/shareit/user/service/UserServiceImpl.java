package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        ensureUserExists(userDto.getId());
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.updateUser(user));
    }

    @Override
    public void deleteUser(Long id) {
        ensureUserExists(id);
        userRepository.deleteUser(id);
    }

    private void ensureUserExists(Long id) {
        userRepository.getUserById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
