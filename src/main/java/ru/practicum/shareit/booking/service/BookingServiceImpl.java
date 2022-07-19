package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.exception.BookingAccessDeniedException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.WrongBookingTimeException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking createBooking(BookingDtoCreate bookingDtoCreate) {
        User requester = userService.getUserByIdOrThrow(bookingDtoCreate.getRequesterId());
        Item item = itemService.getItemByIdOrThrow(bookingDtoCreate.getItemID());
        if (!item.isAvailable()) {
            throw new ItemUnavailableException(item.getId());
        }

        if (bookingDtoCreate.getStart().isBefore(LocalDate.now())) {
            throw new WrongBookingTimeException("Start time is in the past");
        }

        if (bookingDtoCreate.getEnd().isBefore(LocalDate.now())) {
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
        itemService.getAndCheckPermissions(booking.getItem().getId(), approverId);
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
        if (!booking.getBooker().getId().equals(requesterId)
                || !booking.getItem().getOwner().getId().equals(requesterId)) {
            throw new BookingAccessDeniedException();
        }
        return booking;
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
    public Collection<Booking> findAllBookingsByItemOwner(Long requesterId, BookingState state) {
        return null;
    }

    Booking getBookingByIdOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }
}