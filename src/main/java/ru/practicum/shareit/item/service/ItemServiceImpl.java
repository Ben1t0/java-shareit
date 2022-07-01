package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getAllByOwnerId(Long userId) {
        return null;
    }

    @Override
    public ItemDto findItemByName(String name) {
        return null;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        return null;
    }

    @Override
    public void deleteItem(Long id, Long userId) {

    }

    @Override
    public ItemDto getItemById(Long id) {
        return null;
    }

    @Override
    public List<ItemDto> findItemsByQuery(String text) {
        return null;
    }
}