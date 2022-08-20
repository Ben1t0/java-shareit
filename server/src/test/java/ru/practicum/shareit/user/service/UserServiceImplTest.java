package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Test
    void successfullyPatchUser() {
        final UserDto userDto = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();

        userDto.setId(userService.createUser(userDto).getId());

        final UserDto patchForUser = UserDto.builder()
                .id(userDto.getId())
                .email("newemail@email.com")
                .name("patched name")
                .build();

        userService.patchUser(patchForUser);

        assertThat(userService.getUserDtoByOrThrow(userDto.getId()))
                .isNotNull()
                .matches(user -> user.getId().equals(userDto.getId()) &&
                        user.getName().equals(patchForUser.getName()) &&
                        user.getEmail().equals(patchForUser.getEmail()));

        assertThatCode(() -> userService.deleteUser(userDto.getId())).doesNotThrowAnyException();
        assertThat(userService.getAll()).hasSize(0);
    }
}