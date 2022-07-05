package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.Validation;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class BookingDto {
    @NotNull(groups = Validation.OnUpdate.class)
    private Long id;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDate start;
    @NotNull(groups = Validation.OnCreate.class)
    private LocalDate end;
    @NotNull(groups = Validation.OnCreate.class)
    private Item item;
    private Long booker;
    private BookingStatus status;

    @AllArgsConstructor
    static class Item {
        private Long id;
        private String name;
    }
}
