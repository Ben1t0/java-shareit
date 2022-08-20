package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@Valid @RequestBody ItemRequestDto request,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.createRequest(request, userId);
    }

    @GetMapping
    public Collection<ItemRequestResponseDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestResponseDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Positive Integer size) {
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable long requestId) {
        return itemRequestService.getById(requestId, userId);
    }

}
