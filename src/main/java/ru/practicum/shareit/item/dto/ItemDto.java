package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    @NotNull(groups = Validation.OnUpdate.class)
    private Long id;
    @NotNull(groups = {Validation.OnCreate.class, Validation.OnUpdate.class})
    @NotBlank(groups = {Validation.OnCreate.class, Validation.OnUpdate.class},
            message = "Item name can't be blank")
    private String name;
    @NotBlank(groups = {Validation.OnCreate.class, Validation.OnUpdate.class},
            message = "Item description can't be blank")
    private String description;
    @NotNull(groups = {Validation.OnCreate.class, Validation.OnUpdate.class})
    private Boolean available;
    private Long request;

    private Booking lastBooking;
    private Booking nextBooking;

    @AllArgsConstructor
    @Getter
    public static class Booking{
        private Long id;
        private Long bookerId;
    }
}
