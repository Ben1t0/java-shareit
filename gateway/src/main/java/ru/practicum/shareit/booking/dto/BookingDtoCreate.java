package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class BookingDtoCreate {
    @NotNull(groups = Validation.OnCreate.class)
    private Long itemId;
    @NotNull(groups = Validation.OnCreate.class)
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDateTime end;
}
