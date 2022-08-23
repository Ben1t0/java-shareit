package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class CommentDto {
    private String text;
    @JsonIgnore
    private Long authorId;
    @JsonIgnore
    private Long itemId;
    @JsonIgnore
    private LocalDateTime created;
}
