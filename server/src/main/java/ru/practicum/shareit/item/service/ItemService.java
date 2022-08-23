package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemService {
    Collection<ItemDtoWithBookings> getAllByOwnerId(Long userId, Integer from, Integer size);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(ItemDto itemDto, Long userId);

    void deleteItem(Long id, Long userId);

    Item getItemByIdOrThrow(Long id);

    ItemDtoWithBookings getItemByIdWithBookingsOrThrow(Long itemId, Long requesterId);

    Collection<ItemDto> findItemsByQuery(String text, Integer from, Integer size);

    Item getAndCheckPermissions(Long itemId, Long userId);
}
