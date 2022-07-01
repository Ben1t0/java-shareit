package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserRepository {
    UserDto getUserById(Long id);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto);
    void deleteUser(Long id);
}