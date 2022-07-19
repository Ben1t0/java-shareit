package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public Collection<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(@RequestParam() String text) {
        return itemService.findItemsByQuery(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long id) {
        return ItemMapper.toItemDto(itemService.getItemByIdOrThrow(id));
    }

    @PostMapping
    @Validated(Validation.OnCreate.class)
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ItemMapper.toItemDto(itemService.createItem(itemDto, ownerId));
    }

    @PutMapping
    @Validated(Validation.OnPatch.class)
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.patchItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    @Validated(Validation.OnPatch.class)
    public ItemDto patchItem(@PathVariable("itemId") Long id,
                             @Valid @RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(id);
        return ItemMapper.toItemDto(itemService.patchItem(itemDto, userId));
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
    }

}
