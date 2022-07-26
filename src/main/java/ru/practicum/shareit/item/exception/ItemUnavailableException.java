package ru.practicum.shareit.item.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(Long itemId) {
        super(String.format("Item with id %d is unavailable for booking", itemId));
    }
}
