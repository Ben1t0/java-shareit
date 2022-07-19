package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDate;

@Getter
@Builder
public class BookingDto {
    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @AllArgsConstructor
    @Getter
    static class Item {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    static class User{
        private Long id;
    }
}
