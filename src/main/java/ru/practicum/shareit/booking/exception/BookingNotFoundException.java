package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException{
    public BookingNotFoundException(Long bookingId) {
        super(String.format("Booking with id %d not found", bookingId));
    }
}
