package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private BookingDtoCreate bookingDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.parse(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        bookingDto = new BookingDtoCreate(20L,
                start.plusSeconds(1),
                start.plusSeconds(2));
    }

    @Test
    void successfullyCreateBookingTest200() throws Exception {
        when(bookingService.createBooking(any(), any()))
                .thenAnswer(invocation -> BookingDto.builder()
                        .id(1L)
                        .start(bookingDto.getStart())
                        .end(bookingDto.getEnd())
                        .booker(new BookingDto.User(99L))
                        .item(new BookingDto.Item(bookingDto.getItemId(), "item id 20"))
                        .status(BookingStatus.WAITING)
                        .build());

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", "99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(99)))
                .andExpect(jsonPath("$.item.id", is(20)))
                .andExpect(jsonPath("$.item.name", is("item id 20")))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.name())));
        verify(bookingService, Mockito.times(1)).createBooking(any(), any());
    }

    @Test
    void successfullySetApprove() throws Exception {
        mvc.perform(patch("/bookings/{bookingId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 99)
                        .param("approved", "true"))
                .andExpect(status().isOk());
        verify(bookingService, Mockito.times(1)).setApprove(1L, true, 99L);
    }

    @Test
    void successfullyGetBookingById() throws Exception {
        mvc.perform(get("/bookings/{bookingId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());
        verify(bookingService, Mockito.times(1)).findBookingById(1L, 99L);
    }

    @Test
    void throwExceptionWhenNotFoundBookingById() throws Exception {
        when(bookingService.findBookingById(any(), any()))
                .thenThrow(new BookingNotFoundException(1L));

        mvc.perform(get("/bookings/{bookingId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isNotFound());

        verify(bookingService, Mockito.times(1))
                .findBookingById(1L, 99L);
    }

    @Test
    void successfullyGetAllBookingByBookerId() throws Exception {
        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());
        verify(bookingService, Mockito.times(1))
                .findAllBookingsByBookerIdAndStateWithPagination(99L, BookingState.ALL.name(), 0, 20);
    }


    @Test
    void successfullyGetAllBookingByItemOwnerId() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());
        verify(bookingService, Mockito.times(1))
                .findAllBookingsByItemOwnerAndStateWithPagination(99L, BookingState.ALL.name(), 0, 20);
    }
}