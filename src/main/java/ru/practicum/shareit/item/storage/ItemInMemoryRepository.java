package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemInMemoryRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long nextID = 1;

    @Override
    public Collection<Item> getAllByOwnerId(long ownerId) {
        return items.values().stream().filter(i -> i.getOwner().getId() == ownerId).collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item newItem) {
        newItem.setId(nextID++);
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItem(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> findItemsByQuery(String query) {
        return items.values().stream()
                .filter(i -> i.getName().contains(query)
                        || (i.getDescription() != null && i.getDescription().contains(query)))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        items.remove(id);
    }
}
