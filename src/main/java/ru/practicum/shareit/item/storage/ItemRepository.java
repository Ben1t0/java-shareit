package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Collection<Item> getAllByOwnerId(long ownerId);

    Item createItem(Item newItem);

    Optional<Item> getItem(long id);

    void delete(long id);

    Item update(Item toUpdate);

    Collection<Item> findItemsByQuery(String query);
}
