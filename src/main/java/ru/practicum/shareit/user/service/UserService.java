package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> getAll();

    User getUserByIdOrThrow(Long id);

    User createUser(UserDto userDto);

    User updateUser(UserDto userDto);

    User patchUser(UserDto userDto);

    void deleteUser(Long id);
}
