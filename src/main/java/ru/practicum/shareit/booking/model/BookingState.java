package ru.practicum.shareit.booking.model;

import lombok.ToString;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED
}
