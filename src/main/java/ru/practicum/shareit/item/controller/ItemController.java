package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItems(@RequestParam() String text){
        return itemService.findItemsByQuery(text);
    }
    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long id){
        return itemService.getItemById(id);
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @PutMapping
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemDto, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id,@RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(id, userId);
    }

}
