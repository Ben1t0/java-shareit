package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public Collection<ItemDtoWithBookings> getAllByOwnerId(Long ownerId) {
        userService.getUserByIdOrThrow(ownerId);

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(ItemMapper::toDtoWithBookings)
                .peek(item -> {
                    Booking lastBooking = bookingService.findLastBookingForItem(item.getId());
                    Booking nextBooking = bookingService.findNextBookingForItem(item.getId());
                    if (lastBooking != null) {
                        item.setLastBooking(new ItemDtoWithBookings.Booking(lastBooking.getId(),
                                lastBooking.getBooker().getId()));
                    }
                    if (nextBooking != null) {
                        item.setNextBooking(new ItemDtoWithBookings.Booking(nextBooking.getId(),
                                nextBooking.getBooker().getId()));
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userService.getUserByIdOrThrow(userId));

        return ItemMapper.toDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        userService.getUserByIdOrThrow(userId);
        getAndCheckPermissions(itemDto.getId(), userId);
        return ItemMapper.toDto(itemRepository.save(ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, Long userId) {
        userService.getUserByIdOrThrow(userId);
        Item item = getAndCheckPermissions(itemDto.getId(), userId);

        Item toUpdate = Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .comments(item.getComments())
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

        return ItemMapper.toDto(itemRepository.save(toUpdate));
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
    public ItemDtoWithBookings getItemByIdWithBookingsOrThrow(Long itemId, Long requesterId) {
        Item item = getItemByIdOrThrow(itemId);
        if (item.getOwner().getId().equals(requesterId)) {
            Booking lastBooking = bookingService.findLastBookingForItem(item.getId());
            Booking nextBooking = bookingService.findNextBookingForItem(item.getId());
            ItemDtoWithBookings itemDto = ItemMapper.toDtoWithBookings(item);
            if (lastBooking != null) {
                itemDto.setLastBooking(new ItemDtoWithBookings.Booking(lastBooking.getId(),
                        lastBooking.getBooker().getId()));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(new ItemDtoWithBookings.Booking(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
            return itemDto;
        } else {
            return ItemMapper.toDtoWithBookings(item);
        }
    }

    @Override
    public Collection<ItemDto> findItemsByQuery(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findItemsByQuery(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
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