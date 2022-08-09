package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
public class ItemRequestResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private Collection<Item> items;

    @Builder
    @Getter
    @AllArgsConstructor
    static class Item {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private Long requestId;
    }
}
