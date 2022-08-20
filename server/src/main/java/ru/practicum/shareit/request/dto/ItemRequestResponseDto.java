package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemRequestResponseDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Item {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private Long requestId;
    }
}
