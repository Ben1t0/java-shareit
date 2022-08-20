package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long userId) {
        return userClient.getUserById(userId);
    }

    @PostMapping
    @Validated({Validation.OnCreate.class})
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        return userClient.createUser(userDto);
    }

    @PutMapping
    @Validated({Validation.OnUpdate.class})
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto userDto) {
        return userClient.updateUser(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Validation.OnPatch.class})
    public ResponseEntity<Object> patchUser(@PathVariable("id") Long userId, @Valid @RequestBody UserDto userDto) {
        return userClient.patchUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId) {
        return userClient.deleteUser(userId);
    }
}
