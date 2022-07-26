package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BookingDtoCreate {
    @NotNull(groups = Validation.OnCreate.class)
    private Long itemId;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDateTime start;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDateTime end;
    @Setter
    private Long requesterId;
}
