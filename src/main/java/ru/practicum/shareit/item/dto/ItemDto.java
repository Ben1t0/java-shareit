package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    @NotNull(groups = Validation.OnUpdate.class)
    private Long id;
    @NotNull(groups = Validation.OnCreate.class)
    @NotBlank(groups = Validation.OnCreate.class, message = "Item name can't be blank")
    private String name;
    private String description;
    @NotNull
    private boolean available;
    private Long request;
}
