package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    Collection<ItemDto> getAllByOwnerId(Long userId);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId);

    void deleteItem(Long id, Long userId);

    ItemDto getItemById(Long id);

    Collection<ItemDto> findItemsByQuery(String text);
}
