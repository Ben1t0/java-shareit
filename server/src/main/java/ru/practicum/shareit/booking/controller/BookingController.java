package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingService.createBooking(bookingDtoCreate, requesterId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@PathVariable("bookingId") Long id,
                                 @RequestParam(name = "approved") boolean isApproved,
                                 @RequestHeader("X-Sharer-User-Id") Long approverId) {
        return bookingService.setApprove(id, isApproved, approverId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable("bookingId") Long id,
                                     @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingService.findBookingById(id, requesterId);
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByBookerId(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return bookingService.findAllBookingsByBookerIdAndStateWithPagination(requesterId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByItemOwner(
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "20") Integer size) {
        return bookingService.findAllBookingsByItemOwnerAndStateWithPagination(requesterId, state, from, size);
    }
}
