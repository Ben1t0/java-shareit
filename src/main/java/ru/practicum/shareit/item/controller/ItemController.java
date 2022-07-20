package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import java.awt.print.Book;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;
    private final BookingService bookingService;

    @GetMapping
    public Collection<ItemDtoWithBookings> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoWithBookings)
                .peek(item -> {
                    Booking lastBooking = bookingService.findLastBookingForItem(item.getId());
                    Booking nextBooking = bookingService.findNextBookingForItem(item.getId());
                    if (lastBooking != null) {
                        item.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
                    }
                    if (nextBooking != null) {
                        item.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(@RequestParam() String text) {
        return itemService.findItemsByQuery(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookings getItemById(@PathVariable("itemId") Long id,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.getItemByIdOrThrow(id);
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingService.findLastBookingForItem(item.getId());
            Booking nextBooking = bookingService.findNextBookingForItem(item.getId());
            ItemDtoWithBookings itemDto = ItemMapper.toItemDtoWithBookings(item);
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDto.Booking(lastBooking.getId(), lastBooking.getBooker().getId()));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDto.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            return itemDto;
        } else {
            return ItemMapper.toItemDtoWithBookings(item);
        }
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
