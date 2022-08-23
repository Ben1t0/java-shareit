package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public Collection<ItemDtoWithBookings> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return itemService.getAllByOwnerId(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(
            @RequestParam() String text,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return itemService.findItemsByQuery(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable("itemId") Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemByIdWithBookingsOrThrow(id, userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PutMapping
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable("itemId") Long id,
                             @RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(id);
        return itemService.patchItem(itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@PathVariable("itemId") Long itemId,
                                            @RequestBody CommentDto commentDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        LocalDateTime created = LocalDateTime.now();
        commentDto.setCreated(created);
        commentDto.setAuthorId(userId);
        commentDto.setItemId(itemId);
        return commentService.createComment(commentDto);
    }
}
