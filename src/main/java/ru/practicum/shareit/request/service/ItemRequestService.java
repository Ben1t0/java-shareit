package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestResponseDto createRequest(ItemRequestDto request, Long userId);

    ItemRequestResponseDto getById(long requestId, long userId);

    Collection<ItemRequestResponseDto> getAllByUser(Long userId);

    Collection<ItemRequestResponseDto> getAll(Long userId, Integer from, Integer size);

    ItemRequest getByIdOrThrow(long requestId);
}
