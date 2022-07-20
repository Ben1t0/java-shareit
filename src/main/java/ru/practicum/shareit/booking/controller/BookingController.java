package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation.Validation;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Validated(Validation.OnCreate.class)
    public BookingDto createBooking(@Valid @RequestBody BookingDtoCreate bookingDtoCreate,
                                    @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        bookingDtoCreate.setRequesterId(requesterId);
        return BookingMapper.toBookingDto(bookingService.createBooking(bookingDtoCreate));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@PathVariable("bookingId") Long id,
                                 @RequestParam(name = "approved") boolean isApproved,
                                 @RequestHeader("X-Sharer-User-Id") Long approverId) {
        return BookingMapper.toBookingDto(bookingService.setApprove(id, isApproved, approverId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable("bookingId") Long id,
                                     @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return BookingMapper.toBookingDto(bookingService.findBookingById(id, requesterId));
    }

    @GetMapping
    public Collection<BookingDto> getAllBookingsByBookerId(
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {

        return bookingService.findAllBookingsByBookerIdAndState(requesterId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByItemOwner(
            @RequestParam(name = "state", defaultValue = "ALL") BookingState state,
            @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return bookingService.findAllBookingsByItemOwnerAndState(requesterId, state).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
