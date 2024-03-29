package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestResponseDto createRequest(ItemRequestDto request, Long userId) {
        User requester = userService.getUserByIdOrThrow(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .description(request.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ItemRequest saved = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toResponseDto(saved);
    }

    @Override
    public ItemRequest getByIdOrThrow(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(requestId));
    }

    @Override
    public ItemRequestResponseDto getById(long requestId, long userId) {
        userService.getUserByIdOrThrow(userId);
        return ItemRequestMapper.toResponseDto(getByIdOrThrow(requestId));
    }

    @Override
    public Collection<ItemRequestResponseDto> getAllByUser(Long userId) {
        userService.getUserByIdOrThrow(userId);
        return itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(userId).stream()
                .map(ItemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestResponseDto> getAll(Long userId, Integer from, Integer size) {

        Sort sortByCreationTime = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = new OffsetBasedPageRequest(from, size, sortByCreationTime);
        return itemRequestRepository.findAllByRequesterIdNot(userId, page).stream()
                .map(ItemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
