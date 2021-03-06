package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    @AllArgsConstructor
    @Getter
    public static class Item {
        private Long id;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    public static class User {
        private Long id;
    }
}
