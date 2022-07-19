package ru.practicum.shareit.booking.model;

import lombok.ToString;

@ToString
public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
