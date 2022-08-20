package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.exception.BookingAlreadyChecked;
import ru.practicum.shareit.booking.exception.BookingUnknownStateException;
import ru.practicum.shareit.booking.exception.WrongBookingTimeException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceImplUnitTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void throwExceptionWhenBookingStartTimeInPast() {
        BookingDtoCreate dtoMock = Mockito.mock(BookingDtoCreate.class);

        when(dtoMock.getStart()).thenReturn(LocalDateTime.now().minusDays(2));

        assertThatThrownBy(() -> bookingService.createBooking(dtoMock, 1L))
                .isInstanceOf(WrongBookingTimeException.class)
                .hasMessageContaining("Start time is in the past");

    }

    @Test
    void throwExceptionWhenBookingEndTimeInPast() {
        BookingDtoCreate dtoMock = Mockito.mock(BookingDtoCreate.class);

        when(dtoMock.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(dtoMock.getEnd()).thenReturn(LocalDateTime.now().minusDays(2));


        assertThatThrownBy(() -> bookingService.createBooking(dtoMock, 1L))
                .isInstanceOf(WrongBookingTimeException.class)
                .hasMessageContaining("End time is in the past");

    }

    @Test
    void throwExceptionWhenBookingEndTimeBeforeStart() {
        BookingDtoCreate dtoMock = Mockito.mock(BookingDtoCreate.class);

        when(dtoMock.getStart()).thenReturn(LocalDateTime.now().plusDays(2));
        when(dtoMock.getEnd()).thenReturn(LocalDateTime.now().plusDays(1));


        assertThatThrownBy(() -> bookingService.createBooking(dtoMock, 1L))
                .isInstanceOf(WrongBookingTimeException.class)
                .hasMessageContaining("End time is before start time");

    }

    @Test
    void throwExceptionNotFoundWhenTryBookTheirOwnItem() {
        BookingDtoCreate bookingMock = Mockito.mock(BookingDtoCreate.class);
        User userMock = Mockito.mock(User.class);
        Item itemMock = Mockito.mock(Item.class);

        when(bookingMock.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingMock.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(bookingMock.getItemId()).thenReturn(2L);

        when(userMock.getId()).thenReturn(1L);
        when(itemMock.getOwner()).thenReturn(userMock);
        when(itemMock.getId()).thenReturn(2L);

        when(itemRepository.findById(2L)).thenReturn(Optional.of(itemMock));

        assertThatThrownBy(() -> bookingService.createBooking(bookingMock, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item with id 2 not found");
    }

    @Test
    void throwExceptionWhenTryBookNotAvailableItem() {
        BookingDtoCreate bookingMock = Mockito.mock(BookingDtoCreate.class);
        User userMock = Mockito.mock(User.class);
        Item itemMock = Mockito.mock(Item.class);

        when(bookingMock.getStart()).thenReturn(LocalDateTime.now().plusDays(1));
        when(bookingMock.getEnd()).thenReturn(LocalDateTime.now().plusDays(2));
        when(bookingMock.getItemId()).thenReturn(2L);

        when(userMock.getId()).thenReturn(1L);
        when(itemMock.getOwner()).thenReturn(userMock);
        when(itemMock.getId()).thenReturn(2L);
        when(itemMock.isAvailable()).thenReturn(false);

        when(itemRepository.findById(2L)).thenReturn(Optional.of(itemMock));

        assertThatThrownBy(() -> bookingService.createBooking(bookingMock, 4L))
                .isInstanceOf(ItemUnavailableException.class)
                .hasMessageContaining("Item with id 2 is unavailable for booking");
    }

    @Test
    void throwExceptionApproveAlreadyApprovedBooking() {
        Booking bookingMock = Mockito.mock(Booking.class);
        User userMock = Mockito.mock(User.class);
        Item itemMock = Mockito.mock(Item.class);

        when(bookingMock.getItem()).thenReturn(itemMock);
        when(bookingMock.getStatus()).thenReturn(BookingStatus.APPROVED);

        when(userMock.getId()).thenReturn(3L);
        when(itemMock.getOwner()).thenReturn(userMock);
        when(itemMock.getId()).thenReturn(2L);
        when(itemMock.isAvailable()).thenReturn(false);

        when(itemRepository.findById(2L)).thenReturn(Optional.of(itemMock));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingMock));

        assertThatThrownBy(() -> bookingService.setApprove(1L, true, 3L))
                .isInstanceOf(BookingAlreadyChecked.class)
                .hasMessageContaining("Booking already checked");
    }

    @Test
    void throwExceptionWhenUnknownBookingState() {
        assertThatThrownBy(() -> bookingService.findAllBookingsByBookerIdAndStateWithPagination(1L,
                "UNKNOWN_STATE", Pageable.unpaged()))
                .isInstanceOf(BookingUnknownStateException.class)
                .hasMessageContaining("Unknown state: UNKNOWN_STATE");
    }
}
