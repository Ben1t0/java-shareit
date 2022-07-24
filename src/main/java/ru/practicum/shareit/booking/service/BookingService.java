package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDto setApprove(Long bookingId, boolean approveState, Long approverId);

    BookingDto findBookingById(Long bookingId, Long requesterId);

    Collection<BookingDto> findAllBookingsByBookerIdAndState(Long requesterId, BookingState state);

    Collection<BookingDto> findAllBookingsByItemOwnerAndState(Long requesterId, BookingState state);

    Booking findLastBookingForItem(Long itemId);

    Booking findNextBookingForItem(Long itemId);
}
