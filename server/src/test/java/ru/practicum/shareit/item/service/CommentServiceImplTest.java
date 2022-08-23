package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentServiceImplTest {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private BookingService bookingService;

    @Transactional
    @Test
    void successfullyCreateComment() throws InterruptedException {
        final UserDto owner = UserDto.builder()
                .name("vasya")
                .email("vasya@ya.ru")
                .build();

        final UserDto commentator = UserDto.builder()
                .name("petya")
                .email("petya@ya.ru")
                .build();

        owner.setId(userService.createUser(owner).getId());
        commentator.setId(userService.createUser(commentator).getId());

        final ItemDto item1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true).build();

        item1.setId(itemService.createItem(item1, owner.getId()).getId());


        BookingDtoCreate bookPast = new BookingDtoCreate(item1.getId(), LocalDateTime.now().plusNanos(5000000),
                LocalDateTime.now().plusNanos(10000000));

        bookingService.createBooking(bookPast, commentator.getId());

        //Требуется задержка перед проверкой чтобы бронирования стали "прошедшими"
        Thread.sleep(50);
        CommentDto commentDto = CommentDto.builder()
                .text("First comment")
                .itemId(item1.getId())
                .authorId(commentator.getId())
                .created(LocalDateTime.now())
                .build();

        assertThat(commentService.createComment(commentDto)).hasFieldOrPropertyWithValue("id",1L);
    }
}