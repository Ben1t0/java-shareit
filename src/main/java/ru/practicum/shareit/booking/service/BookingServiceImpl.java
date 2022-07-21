package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.exception.BookingAlreadyChecked;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingTimeException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public Booking findLastBookingForItem(Long itemId) {
        return bookingRepository.findAllByItemIdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now()).stream()
                .findFirst().orElse(null);
    }

    @Override
    public Booking findNextBookingForItem(Long itemId) {
        return bookingRepository.findAllByItemIdAndStartAfterOrderByStartDesc(itemId, LocalDateTime.now()).stream()
                .findFirst().orElse(null);
    }

    private final ItemService itemService;

    @Override
    public Booking createBooking(BookingDtoCreate bookingDtoCreate) {
        User requester = userService.getUserByIdOrThrow(bookingDtoCreate.getRequesterId());
        Item item = itemService.getItemByIdOrThrow(bookingDtoCreate.getItemId());

        if (item.getOwner().getId().equals(bookingDtoCreate.getRequesterId())) {
            throw new ItemNotFoundException(item.getId());
        }

        if (!item.isAvailable()) {
            throw new ItemUnavailableException(item.getId());
        }

        if (bookingDtoCreate.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongBookingTimeException("Start time is in the past");
        }

        if (bookingDtoCreate.getEnd().isBefore(LocalDateTime.now())) {
            throw new WrongBookingTimeException("End time is in the past");
        }

        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new WrongBookingTimeException("End time is before start time");
        }

        Booking booking = Booking.builder()
                .status(BookingStatus.WAITING)
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .booker(requester)
                .item(item)
                .build();
        return bookingRepository.save(booking);
    }

    @Override
    public Booking setApprove(Long bookingId, boolean approveState, Long approverId) {
        userService.getUserByIdOrThrow(approverId);
        Booking booking = getBookingByIdOrThrow(bookingId);
        if (!booking.getItem().getOwner().getId().equals(approverId)) {
            throw new BookingNotFoundException(bookingId);
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BookingAlreadyChecked();
        }
        if (approveState) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(Long bookingId, Long requesterId) {
        userService.getUserByIdOrThrow(requesterId);
        Booking booking = getBookingByIdOrThrow(bookingId);

        if (booking.getBooker().getId().equals(requesterId)
                || booking.getItem().getOwner().getId().equals(requesterId)) {
            return booking;
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Override
    public Collection<Booking> findAllBookingsByBookerIdAndState(Long requesterId, BookingState state) {
        userService.getUserByIdOrThrow(requesterId);
        switch (state) {
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(requesterId,
                        BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(requesterId,
                        BookingStatus.REJECTED);
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(requesterId,
                        LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(requesterId,
                        LocalDateTime.now());
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(requesterId,
                        now, now);
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(requesterId);
        }
    }

    @Override
    public Collection<Booking> findAllBookingsByItemOwnerAndState(Long requesterId, BookingState state) {
        userService.getUserByIdOrThrow(requesterId);
        Collection<Long> itemsId = itemService.getAllByOwnerId(requesterId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        switch (state) {
            case WAITING:
                return bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(itemsId, BookingStatus.WAITING);
            case REJECTED:
                return bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(itemsId, BookingStatus.REJECTED);
            case PAST:
                return bookingRepository.findAllByItemIdInAndEndIsBeforeOrderByStartDesc(itemsId,
                        LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(itemsId,
                        LocalDateTime.now());
            case CURRENT:
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(itemsId,
                        now, now);
            default:
                return bookingRepository.findAllByItemIdInOrderByStartDesc(itemsId);
        }
    }

    Booking getBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}