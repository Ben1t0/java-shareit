package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(Long requestId) {
        super(String.format("Item request with id %d not found", requestId));
    }
}
