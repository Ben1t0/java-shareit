package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null)
                .build();
    }
}
