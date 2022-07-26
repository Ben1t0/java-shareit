package ru.practicum.shareit.booking.exception;

public class WrongBookingTimeException extends RuntimeException {
    public WrongBookingTimeException(String message) {
        super(message);
    }
}
