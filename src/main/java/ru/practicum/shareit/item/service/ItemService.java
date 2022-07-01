package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllByOwnerId(Long userId);

    ItemDto findItemByName(String name);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long userId);

    void deleteItem(Long id, Long userId);

    ItemDto getItemById(Long id);

    List<ItemDto> findItemsByQuery(String text);
}
