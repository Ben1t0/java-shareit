package ru.practicum.shareit.booking.exception;

public class BookingAccessDeniedException extends RuntimeException{
    public BookingAccessDeniedException(){
        super("ACCESS DENIED: you are not an owner of booking or booked item");
    }

}
