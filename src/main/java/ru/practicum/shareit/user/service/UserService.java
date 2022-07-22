package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAll();

    User getUserByIdOrThrow(Long id);

    UserDto getUserDtoByOrThrow(Long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto patchUser(UserDto userDto);

    void deleteUser(Long id);
}
