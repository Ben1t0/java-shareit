package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    void successfullyGetAllRequestsByUser() {
        final UserDto itemOwner = UserDto.builder()
                .name("vasya")
                .email("vasya@ya.ru")
                .build();

        final UserDto requester = UserDto.builder()
                .name("petya")
                .email("petya@ya.ru")
                .build();

        final UserDto requester2 = UserDto.builder()
                .name("senya")
                .email("senya@ya.ru")
                .build();

        itemOwner.setId(userService.createUser(itemOwner).getId());
        requester.setId(userService.createUser(requester).getId());
        requester2.setId(userService.createUser(requester2).getId());

        ItemRequestResponseDto request = itemRequestService
                .createRequest(new ItemRequestDto("need drill"), requester.getId());

        itemRequestService.createRequest(new ItemRequestDto("need brush"), requester.getId());

        final ItemDto item1 = ItemDto.builder().name("Дрель").description("Простая дрель").available(true)
                .requestId(request.getId()).build();

        itemRequestService.createRequest(new ItemRequestDto("need hammer"), requester2.getId());


        item1.setId(itemService.createItem(item1, itemOwner.getId()).getId());

        assertThat(itemRequestService.getAllByUser(requester.getId())).hasSize(2)
                .element(0)
                .matches(req -> req.getId() == 1L &&
                        req.getDescription().equals("need drill") &&
                        req.getItems().get(0).getName().equals(item1.getName()) &&
                        req.getItems().get(0).getDescription().equals(item1.getDescription()) &&
                        req.getItems().get(0).isAvailable());

        assertThat(itemRequestService.getAllByUser(requester2.getId())).hasSize(1)
                .element(0)
                .matches(i -> i.getId() == 3L && i.getDescription().equals("need hammer"));
    }
}