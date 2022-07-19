package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


public interface ItemService {
    Collection<Item> getAllByOwnerId(Long userId);

    Item createItem(ItemDto itemDto, Long userId);

    Item updateItem(ItemDto itemDto, Long userId);

    Item patchItem(ItemDto itemDto, Long userId);

    void deleteItem(Long id, Long userId);

    Item getItemByIdOrThrow(Long id);

    Collection<Item> findItemsByQuery(String text);

    Item getAndCheckPermissions(Long itemId, Long userId);
}
