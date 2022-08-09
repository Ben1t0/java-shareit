package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestResponseDto createRequest(ItemRequestDto request, Long userId);

    ItemRequestResponseDto getById(long requestId, long userId);

    Collection<ItemRequestResponseDto> getAllByUser(Long userId);

    Collection<ItemRequestResponseDto> getAll(Long userId, Integer from, Integer size);

    ItemRequest getByIdOrThrow(long requestId);
}
