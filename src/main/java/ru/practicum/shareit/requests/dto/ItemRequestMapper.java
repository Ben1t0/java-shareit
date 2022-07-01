package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(new ItemRequestDto.User(
                        (itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null),
                        (itemRequest.getRequestor() != null ? itemRequest.getRequestor().getName() : null)))
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .created(itemRequestDto.getCreated())
                .description(itemRequestDto.getDescription())
                .build();
    }
}
