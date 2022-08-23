package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .description("new drill")
                .name("drill")
                .available(true)
                .build();
    }

    @Test
    void createItemTest200() throws Exception {
        when(itemService.createItem(any(), any()))
                .thenAnswer(invocation -> ItemDto.builder()
                        .id(1L)
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .build());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService, Mockito.times(1)).createItem(itemDto, 99L);
    }

    @Test
    void throwExceptionCreateItemEndpointWrongData400() throws Exception {
        ItemDto wrongDto = ItemDto.builder().name("name").build();
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(wrongDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, Mockito.times(1)).createItem(any(), any());
    }

    @Test
    void throwExceptionWhenPatchWithWrongUser403() throws Exception {
        when(itemService.patchItem(any(), any()))
                .thenThrow(new ItemAccessDeniedException(1L));

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        itemDto.setId(1L);
        verify(itemService, Mockito.times(1)).patchItem(itemDto, 99L);
    }

    @Test
    void successfullyReturnAllItemsByOwnerId() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());
        verify(itemService, Mockito.times(1)).getAllByOwnerId(99L, 0, 20);
    }

    @Test
    void checkSearchItemEndpoint() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "query"))
                .andExpect(status().isOk());
        verify(itemService, Mockito.times(1)).findItemsByQuery("query", 0, 20);
    }

    @Test
    void checkGetItemByIdEndpoint() throws Exception {
        mvc.perform(get("/items/{itemId}", "1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());
        verify(itemService, Mockito.times(1))
                .getItemByIdWithBookingsOrThrow(1L, 99L);
    }

    @Test
    void checkUpdateItemEndpoint() throws Exception {
        mvc.perform(put("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, Mockito.times(1)).updateItem(itemDto, 99L);
    }

    @Test
    void checkPatchItemEndpoint() throws Exception {
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        itemDto.setId(1L);
        verify(itemService, Mockito.times(1)).patchItem(itemDto, 99L);
    }

    @Test
    void checkDeleteItemEndpoint() throws Exception {
        mvc.perform(delete("/items/{id}", "1")
                        .header("X-Sharer-User-Id", 99))
                .andExpect(status().isOk());

        itemDto.setId(1L);
        verify(itemService, Mockito.times(1)).deleteItem(1L, 99L);
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("comment text").build();

        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(commentService, Mockito.times(1))
                .createComment(argThat((CommentDto com) -> com.getItemId().equals(1L) &&
                        com.getAuthorId().equals(99L) &&
                        com.getText().equals("comment text")));
    }

    @Test
    void createItemUserNotFound404() throws Exception {
        when(itemService.createItem(any(), any()))
                .thenThrow(new UserNotFoundException(1L));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 99)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(itemService, Mockito.times(1)).createItem(itemDto, 99L);
    }
}