package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    Booking createBooking(BookingDtoCreate bookingDtoCreate);

    Booking setApprove(Long bookingId, boolean approveState, Long approverId);

    Booking findBookingById(Long bookingId, Long requesterId);

    Collection<Booking> findAllBookingsByBookerIdAndState(Long requesterId, BookingState state);

    Collection<Booking> findAllBookingsByItemOwnerAndState(Long requesterId, BookingState state);

    Booking findLastBookingForItem(Long itemId);

    Booking findNextBookingForItem(Long itemId);
}
