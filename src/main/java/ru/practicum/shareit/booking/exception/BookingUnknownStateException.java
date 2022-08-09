package ru.practicum.shareit.booking.exception;

public class BookingUnknownStateException extends RuntimeException {
    public BookingUnknownStateException(String state) {
        super("Unknown state: " + state);
    }
}
