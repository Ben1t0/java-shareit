package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exception.BookingAlreadyChecked;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingUnknownStateException;
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
        return bookingRepository.findLastBookingByItemIdOrderEndDesc(itemId, LocalDateTime.now());
    }

    @Override
    public Booking findNextBookingForItem(Long itemId) {
        return bookingRepository.findNextBookingByItemIdOrderEndAsc(itemId, LocalDateTime.now());
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

        if (booking.getBooker().getId().equals(requesterId) || isItemOwner(booking.getItem(), requesterId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new BookingNotFoundException(bookingId);
        }
    }


    @Override
    public Collection<BookingDto> findAllBookingsByBookerIdAndStateWithPagination(
            Long requesterId, String state, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        return findAllBookingsByBookerIdAndStateWithPagination(requesterId, state, page);
    }

    @Override
    public Collection<BookingDto> findAllBookingsByBookerIdAndStateWithPagination(Long requesterId, String state,
                                                                                  Pageable page) {
        BookingState bookingState = validBookingStateOrThrow(state);
        userService.getUserByIdOrThrow(requesterId);
        Collection<Booking> bookings;

        switch (bookingState) {
            case WAITING:
                bookings = bookingRepository.findAllUserBookingsWithStatusOrderStartDesc(requesterId,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllUserBookingsWithStatusOrderStartDesc(requesterId,
                        BookingStatus.REJECTED, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllUserBookingsBeforeDateOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllUserBookingsAfterDateOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllUserBookingsAtDateOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findAllUserBookingsOrderStartDesc(requesterId, page);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllBookingsByItemOwnerAndStateWithPagination(Long requesterId, String state,
                                                                                   Integer from, Integer size) {
        BookingState bookingState = validBookingStateOrThrow(state);
        userService.getUserByIdOrThrow(requesterId);
        Collection<Booking> bookings;
        Pageable page = PageRequest.of(from, size);
        switch (bookingState) {
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderStartDesc(requesterId,
                        BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderStartDesc(requesterId,
                        BookingStatus.REJECTED, page);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdInThePastOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdInTheFutureOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdCurrentDateOrderStartDesc(requesterId,
                        LocalDateTime.now(), page);
                break;
            default:
                bookings = bookingRepository.findAllByItemOwnerIdOrderStartDesc(requesterId, page);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private BookingState validBookingStateOrThrow(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new BookingUnknownStateException(state);
        }
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