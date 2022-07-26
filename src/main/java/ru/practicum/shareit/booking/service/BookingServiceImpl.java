package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
import ru.practicum.shareit.item.storage.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Override
    public Booking findLastBookingForItem(Long itemId) {
        return bookingRepository.findLastBookingByItemId(itemId, LocalDateTime.now());
    }

    @Override
    public Booking findNextBookingForItem(Long itemId) {
        return bookingRepository.findNextBookingByItemId(itemId, LocalDateTime.now());
    }


    @Override
    public BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId) {
        validateBookingDtoCreate(bookingDtoCreate);
        User booker = userService.getUserByIdOrThrow(userId);
        Item item = validateAndGetItem(bookingDtoCreate.getItemId(), userId);

        Booking booking = Booking.builder()
                .status(BookingStatus.WAITING)
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .booker(booker)
                .item(item)
                .build();
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setApprove(Long bookingId, boolean approveState, Long approverId) {
        userService.getUserByIdOrThrow(approverId);
        Booking booking = getBookingByIdOrThrow(bookingId);
        if (!isItemOwner(booking.getItem(), approverId)) {
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
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long requesterId) {
        userService.getUserByIdOrThrow(requesterId);
        Booking booking = getBookingByIdOrThrow(bookingId);

        if (booking.getBooker().getId().equals(requesterId)
                || isItemOwner(booking.getItem(), requesterId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }

    @Override
    public Collection<BookingDto> findAllBookingsByBookerIdAndState(Long requesterId, BookingState state) {
        userService.getUserByIdOrThrow(requesterId);
        Collection<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllUserBookingsWithStatus(requesterId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllUserBookingsWithStatus(requesterId, BookingStatus.REJECTED);
                break;
            case PAST:
                bookings = bookingRepository.findAllUserBookingsBeforeDate(requesterId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllUserBookingsAfterDate(requesterId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllUserBookingsAtDate(requesterId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllUserBookings(requesterId);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllBookingsByItemOwnerAndState(Long requesterId, BookingState state) {
        userService.getUserByIdOrThrow(requesterId);
        Collection<Booking> bookings;
        switch (state) {
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(requesterId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(requesterId, BookingStatus.REJECTED);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdInThePast(requesterId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdInTheFuture(requesterId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdCurrentDate(requesterId, LocalDateTime.now());
                break;
            default:
                bookings = bookingRepository.findAllByItemOwnerId(requesterId);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private Booking getBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private boolean isItemOwner(Item item, long userId) {
        return item.getOwner().getId().equals(userId);
    }

    private void validateBookingDtoCreate(BookingDtoCreate bookingDtoCreate) {
        if (bookingDtoCreate.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongBookingTimeException("Start time is in the past");
        }

        if (bookingDtoCreate.getEnd().isBefore(LocalDateTime.now())) {
            throw new WrongBookingTimeException("End time is in the past");
        }

        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new WrongBookingTimeException("End time is before start time");
        }
    }

    private Item validateAndGetItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        if (isItemOwner(item, userId)) {
            throw new ItemNotFoundException(item.getId());
        }

        if (!item.isAvailable()) {
            throw new ItemUnavailableException(item.getId());
        }
        return item;
    }
}