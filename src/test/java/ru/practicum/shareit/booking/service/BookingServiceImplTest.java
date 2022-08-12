package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;


    @Test
    void checkFindAllBookingsByBookerIdAndState() throws InterruptedException {
        final UserDto itemOwner = UserDto.builder()
                .name("vasya")
                .email("vasya@ya.ru")
                .build();

        final UserDto booker = UserDto.builder()
                .name("petya")
                .email("petya@ya.ru")
                .build();

        itemOwner.setId(userService.createUser(itemOwner).getId());
        booker.setId(userService.createUser(booker).getId());

        final ItemDto item1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();

        item1.setId(itemService.createItem(item1, itemOwner.getId()).getId());

        BookingDtoCreate bookPast = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusNanos(50000000),
                LocalDateTime.now().plusNanos(100000000));

        final BookingDto bookDtoPast = bookingService.createBooking(bookPast, booker.getId());

        BookingDtoCreate bookCurrent = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusDays(1));

        final BookingDto bookDtoCurrent = bookingService.createBooking(bookCurrent, booker.getId());

        bookingService.setApprove(bookDtoCurrent.getId(), true, itemOwner.getId());

        BookingDtoCreate bookFuture = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3));

        final BookingDto bookDtoFuture = bookingService.createBooking(bookFuture, booker.getId());

        bookingService.setApprove(bookDtoFuture.getId(), false, itemOwner.getId());

        BookingDto find = bookingService.findBookingById(bookDtoFuture.getId(), booker.getId());

        Thread.sleep(1000);

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.PAST.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoPast.getId()));

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.CURRENT.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoCurrent.getId()));

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.FUTURE.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoFuture.getId()));

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.WAITING.name(), 0, 20))
                .hasSize(1);

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.REJECTED.name(), 0, 20))
                .hasSize(1);

        assertThat(bookingService
                .findAllBookingsByBookerIdAndStateWithPagination(
                        booker.getId(), BookingState.ALL.name(), 0, 20))
                .hasSize(3);

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.PAST.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoPast.getId()));

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.CURRENT.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoCurrent.getId()));

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.FUTURE.name(), 0, 20))
                .hasSize(1)
                .element(0).matches(b -> b.getId().equals(bookDtoFuture.getId()));

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.ALL.name(), 0, 20))
                .hasSize(3);

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.WAITING.name(), 0, 20))
                .hasSize(1);

        assertThat(bookingService
                .findAllBookingsByItemOwnerAndStateWithPagination(
                        itemOwner.getId(), BookingState.REJECTED.name(), 0, 20))
                .hasSize(1);
    }
}