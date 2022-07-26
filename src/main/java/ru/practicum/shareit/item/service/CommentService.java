package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;

public interface CommentService {
    CommentResponseDto createComment(CommentDto commentDto);
}
