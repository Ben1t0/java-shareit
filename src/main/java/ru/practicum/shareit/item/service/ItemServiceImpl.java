package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Collection<Item> getAllByOwnerId(Long ownerId) {
        userService.getUserByIdOrThrow(ownerId);
        return itemRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Item createItem(ItemDto itemDto, Long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userService.getUserByIdOrThrow(userId));

        return itemRepository.save(newItem);
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long userId) {
        userService.getUserByIdOrThrow(userId);
        getAndCheckPermissions(itemDto.getId(), userId);
        return itemRepository.save(ItemMapper.toItem(itemDto));
    }

    @Override
    public Item patchItem(ItemDto itemDto, Long userId) {
        userService.getUserByIdOrThrow(userId);
        Item item = getAndCheckPermissions(itemDto.getId(), userId);

        Item toUpdate = Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();

        if (itemDto.getDescription() != null) {
            toUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getName() != null) {
            toUpdate.setName(itemDto.getName());
        }

        if (itemDto.getAvailable() != null) {
            toUpdate.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(toUpdate);
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        getAndCheckPermissions(id, userId);
        itemRepository.deleteById(id);
    }

    @Override
    public Item getItemByIdOrThrow(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    @Override
    public Collection<Item> findItemsByQuery(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findItemsByQuery(text);
        }
    }

    @Override
    public Item getAndCheckPermissions(Long itemId, Long userId) {
        Item item = getItemByIdOrThrow(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemAccessDeniedException(itemId);
        }
        return item;
    }
}