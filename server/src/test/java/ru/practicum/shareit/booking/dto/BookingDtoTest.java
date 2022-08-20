package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> jsonTester;

    @Test
    void testBookingDtoJsonParse() throws IOException {
        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(now)
                .end(now.plusSeconds(5))
                .booker(new BookingDto.User(99L))
                .item(new BookingDto.Item(20L, "item id 20"))
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDto> dtoJsonContent = jsonTester.write(bookingDto);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(
                Math.toIntExact(bookingDto.getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().toString());
        assertThat(dtoJsonContent).hasJsonPathMapValue("$.item");
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(Math.toIntExact(bookingDto.getItem().getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(dtoJsonContent).hasJsonPathMapValue("$.booker");
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(Math.toIntExact(bookingDto.getBooker().getId()));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.WAITING.name());
    }
}