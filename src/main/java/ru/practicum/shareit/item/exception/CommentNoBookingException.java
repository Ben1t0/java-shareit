package ru.practicum.shareit.item.exception;

public class CommentNoBookingException extends RuntimeException{
    public CommentNoBookingException(){
        super("You didn't book this item");
    }
}
