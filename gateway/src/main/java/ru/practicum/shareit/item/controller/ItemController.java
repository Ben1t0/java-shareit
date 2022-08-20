package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemClient.getAllByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(
            @RequestParam() String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemClient.findItemsByQuery(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") Long id,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItemByIdWithBookings(id, userId);
    }

    @PostMapping
    @Validated(Validation.OnCreate.class)
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.createItem(itemDto, ownerId);
    }

    @PutMapping
    @Validated(Validation.OnUpdate.class)
    public ResponseEntity<Object> updateItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.updateItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(Validation.OnPatch.class)
    public ResponseEntity<Object> patchItem(@PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody ItemDto itemDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.patchItem(itemId, itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable("id") Long id,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.deleteItem(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    @Validated(Validation.OnCreate.class)
    public ResponseEntity<Object> createComment(@PathVariable("itemId") Long itemId,
                                                @Valid @RequestBody CommDto commentDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
