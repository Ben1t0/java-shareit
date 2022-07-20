package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemDtoWithBookings {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;


    private ItemDto.Booking lastBooking;
    private ItemDto.Booking nextBooking;

    @AllArgsConstructor
    @Getter
    static class Booking{
        private Long id;
        private Long bookerId;
    }
}
