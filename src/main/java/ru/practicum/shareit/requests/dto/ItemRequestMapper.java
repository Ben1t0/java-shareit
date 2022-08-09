package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestResponseDto toResponseDto(ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems().stream()
                        .map(item -> ItemRequestResponseDto.Item.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .available(item.isAvailable())
                                .requestId(item.getRequest().getId())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
