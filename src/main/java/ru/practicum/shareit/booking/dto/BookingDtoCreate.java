package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class BookingDtoCreate {
    @NotNull(groups = Validation.OnCreate.class)
    private Long itemID;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDate start;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDate end;
    @Setter
    private Long requesterId;
}
