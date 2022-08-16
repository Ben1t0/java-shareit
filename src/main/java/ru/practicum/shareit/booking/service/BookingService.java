package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(BookingDtoCreate bookingDtoCreate, Long userId);

    BookingDto setApprove(Long bookingId, boolean approveState, Long approverId);

    BookingDto findBookingById(Long bookingId, Long requesterId);

    Collection<BookingDto> findAllBookingsByBookerIdAndStateWithPagination(Long requesterId, String state,
                                                             Integer from, Integer size);

    Collection<BookingDto> findAllBookingsByBookerIdAndStateWithPagination(Long requesterId, String state,
                                                                           Pageable pageable);

    Collection<BookingDto> findAllBookingsByItemOwnerAndStateWithPagination(Long requesterId, String state,
                                                                            Integer from, Integer size);

    Booking findLastBookingForItem(Long itemId);

    Booking findNextBookingForItem(Long itemId);
}
