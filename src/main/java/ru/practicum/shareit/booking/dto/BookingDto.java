package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDate;

/**
 * // TODO .
 */
@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private Long booker;
    private BookingStatus status;

    @AllArgsConstructor
    static class Item{
        private Long id;
        private String name;
    }
}
