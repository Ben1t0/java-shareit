package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;

    private Collection<Comment> comments;

    private Booking lastBooking;
    private Booking nextBooking;

    @AllArgsConstructor
    @Getter
    public static class Booking {
        private Long id;
        private Long bookerId;
    }

    @AllArgsConstructor
    @Getter
    public static class Comment {
        private Long id;
        private String authorName;
        private String text;
        private LocalDateTime created;
    }
}
