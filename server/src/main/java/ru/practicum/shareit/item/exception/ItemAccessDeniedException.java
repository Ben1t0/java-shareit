package ru.practicum.shareit.item.exception;

public class ItemAccessDeniedException extends RuntimeException {
    public ItemAccessDeniedException(long id) {
        super(String.format("ACCESS DENIED: you are not an owner of item %d", id));
    }
}
