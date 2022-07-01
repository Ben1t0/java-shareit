package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "User name can't be blank")
    private String name;
}
