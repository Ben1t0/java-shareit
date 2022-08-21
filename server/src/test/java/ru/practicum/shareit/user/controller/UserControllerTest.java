package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
    }

    @Test
    void createUserTest200() throws Exception {
        when(userService.createUser(any()))
                .thenAnswer(invocation -> UserDto.builder()
                        .id(1L)
                        .name(userDto.getName())
                        .email(userDto.getEmail())
                        .build());

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(userService, Mockito.times(1)).createUser(userDto);
    }

    @Test
    void throwExceptionCreateUserEndpointNoEmailData400() throws Exception {
        UserDto wrongDto = UserDto.builder().name("name").build();
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(wrongDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).createUser(any());
    }

    @Test
    void throwExceptionCreateUserEndpointWrongEmailData400() throws Exception {
        UserDto wrongDto = UserDto.builder().name("name").email("user.com").build();
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(wrongDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).createUser(any());
    }

    @Test
    void throwExceptionCreateUserEndpointExistsEmailData409() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new UserAlreadyExistsException("User with this email already exists"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(userService, Mockito.times(1)).createUser(any());
    }

    @Test
    void successfullyPatchUserTest200() throws Exception {
        mvc.perform(patch("/users/{id}","1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        userDto.setId(1L);
        verify(userService, Mockito.times(1)).patchUser(userDto);
    }

    @Test
    void successfullyUpdateUserTest200() throws Exception {
        userDto.setId(1L);
        mvc.perform(put("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).updateUser(userDto);
    }

    @Test
    void successfullyGetUserByIdEndpoint() throws Exception {
        mvc.perform(get("/users/{id}", "1"))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1))
                .getUserDtoByOrThrow(1L);
    }

    @Test
    void throwExceptionWhenGetNotExistedUser404() throws Exception {
        when(userService.getUserDtoByOrThrow(any()))
                .thenThrow(new UserNotFoundException(1L));
        mvc.perform(get("/users/{id}", "1"))
                .andExpect(status().isNotFound());
        verify(userService, Mockito.times(1))
                .getUserDtoByOrThrow(1L);
    }

    @Test
    void successfullyGetAllUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1))
                .getAll();
    }

    @Test
    void successfullyDeleteUser() throws Exception {
        mvc.perform(delete("/users/{id}", "1"))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1))
                .deleteUser(1L);
    }
}