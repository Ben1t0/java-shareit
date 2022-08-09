package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.exception.CommentNoBookingException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final ItemService itemService;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public CommentResponseDto createComment(CommentDto commentDto) {
        Item item = itemService.getItemByIdOrThrow(commentDto.getItemId());
        User author = userService.getUserByIdOrThrow(commentDto.getAuthorId());
        boolean isBooker = bookingService.findAllBookingsByBookerIdAndStateWithPagination(commentDto.getAuthorId(),
                        BookingState.PAST.toString(), Pageable.unpaged()).stream()
                .anyMatch(b -> b.getItem().getId().equals(commentDto.getItemId()));

        if (isBooker) {
            Comment comment = Comment.builder()
                    .author(author)
                    .item(item)
                    .created(commentDto.getCreated())
                    .text(commentDto.getText())
                    .build();
            item.getComments().add(comment);
            commentRepository.save(comment);
            return CommentMapper.toResponseDto(comment);
        } else {
            throw new CommentNoBookingException();
        }
    }
}
