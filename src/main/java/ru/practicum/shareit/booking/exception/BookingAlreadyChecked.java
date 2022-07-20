package ru.practicum.shareit.booking.exception;

public class BookingAlreadyChecked extends RuntimeException{
    public BookingAlreadyChecked() {
        super("Booking already checked");
    }
}
