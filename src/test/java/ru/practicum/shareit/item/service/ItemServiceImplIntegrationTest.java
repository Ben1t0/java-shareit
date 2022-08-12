package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Transactional
    @Test
    void getAllByOwnerId() throws InterruptedException {
        final UserDto userDto = UserDto.builder()
                .name("vasya")
                .email("vasya@ya.ru")
                .build();

        final UserDto anotherUser = UserDto.builder()
                .name("petya")
                .email("petya@ya.ru")
                .build();

        userDto.setId(userService.createUser(userDto).getId());
        anotherUser.setId(userService.createUser(anotherUser).getId());

        final ItemDto item1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();

        final ItemDto item2 = ItemDto.builder().name("Отвертка").description("Аккумуляторная отвертка").available(true)
                .build();

        item1.setId(itemService.createItem(item1, userDto.getId()).getId());
        item2.setId(itemService.createItem(item2, userDto.getId()).getId());

        BookingDtoCreate book1prev = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(1));
        final BookingDto bookDto1prev = bookingService.createBooking(book1prev, anotherUser.getId());

        BookingDtoCreate book1next = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        final BookingDto bookDto1next = bookingService.createBooking(book1next, anotherUser.getId());

        BookingDtoCreate book2prev = new BookingDtoCreate(item2.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(1));
        final BookingDto bookDto2prev = bookingService.createBooking(book2prev, anotherUser.getId());

        BookingDtoCreate book2next = new BookingDtoCreate(item2.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        final BookingDto bookDto2next = bookingService.createBooking(book2next, anotherUser.getId());


        Thread.sleep(1000);
        Collection<ItemDtoWithBookings> items = itemService.getAllByOwnerId(userDto.getId(), 0, 100);

        ItemDtoWithBookings itemBook = itemService.getItemByIdWithBookingsOrThrow(item1.getId(),userDto.getId());

        assertThat(items).hasSize(2);
        assertThat(items).element(0)
                .matches(i -> i.getId().equals(item1.getId()) &&
                        i.getAvailable().equals(true) &&
                        i.getName().equals(item1.getName()) &&
                        i.getDescription().equals(item1.getDescription()) &&
                        i.getLastBooking().getId().equals(bookDto1prev.getId()) &&
                        i.getLastBooking().getBookerId().equals(anotherUser.getId()) &&
                        i.getNextBooking().getId().equals(bookDto1next.getId()) &&
                        i.getNextBooking().getBookerId().equals(anotherUser.getId()));

        assertThat(items).element(1)
                .matches(i -> i.getId().equals(item2.getId()) &&
                        i.getAvailable().equals(true) &&
                        i.getName().equals(item2.getName()) &&
                        i.getDescription().equals(item2.getDescription()) &&
                        i.getLastBooking().getId().equals(bookDto2prev.getId()) &&
                        i.getLastBooking().getBookerId().equals(anotherUser.getId()) &&
                        i.getNextBooking().getId().equals(bookDto2next.getId()) &&
                        i.getNextBooking().getBookerId().equals(anotherUser.getId()));
    }
}