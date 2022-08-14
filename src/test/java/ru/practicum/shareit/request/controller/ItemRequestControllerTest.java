package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new ItemRequestDto("need drill");
    }

    @Test
    void successfullyCreateRequest() throws Exception {
        String createdTime = "2022-08-11T23:30:12";
        when(itemRequestService.createRequest(any(), any()))
                .thenAnswer(i -> ItemRequestResponseDto.builder()
                        .id(1L)
                        .created(LocalDateTime.parse(createdTime))
                        .description(requestDto.getDescription())
                        .items(new ArrayList<>())
                        .build()
                );

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", "99")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.created", is(createdTime)))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));

        verify(itemRequestService, Mockito.times(1)).createRequest(requestDto, 99L);
    }

    @Test
    void successfullyGetAllRequestsByUser() throws Exception {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "99"))
                .andExpect(status().isOk());

        verify(itemRequestService, Mockito.times(1)).getAllByUser(99L);
    }

    @Test
    void successfullyGetAllRequests() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "99"))
                .andExpect(status().isOk());

        verify(itemRequestService, Mockito.times(1)).getAll(99L, 0, 20);
    }


    @Test
    void successfullyGetRequestId() throws Exception {
        mvc.perform(get("/requests/{requestId}", "1")
                        .header("X-Sharer-User-Id", "99"))
                .andExpect(status().isOk());

        verify(itemRequestService, Mockito.times(1)).getById(1L, 99L);
    }

    @Test
    void throwExceptionWhenNotFound() throws Exception {
        when(itemRequestService.getById(any(long.class), any(long.class)))
                .thenThrow(new ItemRequestNotFoundException(1L));

        mvc.perform(get("/requests/{requestId}", "1")
                        .header("X-Sharer-User-Id", "99"))
                .andExpect(status().isNotFound());

        verify(itemRequestService, Mockito.times(1)).getById(1L, 99L);
    }
}