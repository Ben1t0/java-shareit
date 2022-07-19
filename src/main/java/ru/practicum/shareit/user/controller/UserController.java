package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Long userId) {
        return UserMapper.toUserDto(userService.getUserByIdOrThrow(userId));
    }

    @PostMapping
    @Validated({Validation.OnCreate.class})
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.createUser(userDto));
    }

    @PutMapping
    @Validated({Validation.OnUpdate.class})
    public UserDto updateUser(@Valid @RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.updateUser(userDto));
    }

    @PatchMapping("/{id}")
    @Validated({Validation.OnPatch.class})
    public UserDto patchUser(@PathVariable("id") Long userId, @Valid @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.toUserDto(userService.patchUser(userDto));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
