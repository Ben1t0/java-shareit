package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAllByOwnerId(Long ownerId) {
        ensureUserExists(ownerId);
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)));

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        ensureUserExists(userId);
        Item toUpdate = itemRepository.getItem(itemDto.getId())
                .orElseThrow(() -> new ItemNotFoundException(itemDto.getId()));

        if (!userId.equals(toUpdate.getOwner().getId())) {
            throw new ItemAccessDeniedException(itemDto.getId());
        }

        return ItemMapper.toItemDto(itemRepository.update(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long userId) {
        ensureUserExists(userId);
        Item item = itemRepository.getItem(itemDto.getId())
                .orElseThrow(() -> new ItemNotFoundException(itemDto.getId()));

        if (!userId.equals(item.getOwner().getId())) {
            throw new ItemAccessDeniedException(itemDto.getId());
        }

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

        return ItemMapper.toItemDto(itemRepository.update(toUpdate));
    }

    @Override
    public void deleteItem(Long id, Long userId) {
        Item toDelete = itemRepository.getItem(id).orElseThrow(() -> new ItemNotFoundException(id));
        if (!toDelete.getOwner().getId().equals(userId)) {
            throw new ItemAccessDeniedException(id);
        }
        itemRepository.delete(id);
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getItem(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }

    @Override
    public Collection<ItemDto> findItemsByQuery(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findItemsByQuery(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
    }

    private void ensureUserExists(long id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}