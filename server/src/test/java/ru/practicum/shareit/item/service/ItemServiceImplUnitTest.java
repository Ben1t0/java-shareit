package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemServiceImplUnitTest {

    @Autowired
    private ItemService itemService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemRequestService itemRequestService;


    @Test
    void shouldPathItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("New item name")
                .description("New item desc")
                .available(true)
                .build();

        User user = Mockito.mock(User.class);
        Item item = Mockito.mock(Item.class);

        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(item.getId()).thenReturn(2L);
        when(item.getName()).thenReturn("Old name");
        when(item.getDescription()).thenReturn("Old desc");

        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        assertThat(itemService.patchItem(itemDto, 1L))
                .isNotNull()
                .matches(i -> i.getName().equals(itemDto.getName()) &&
                        i.getDescription().equals(itemDto.getDescription()));
    }

    @Test
    void throwExceptionWhenPatchSomeoneElseItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .build();

        User user = Mockito.mock(User.class);
        Item item = Mockito.mock(Item.class);

        when(item.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(item.getId()).thenReturn(2L);
        when(item.getName()).thenReturn("Old name");
        when(item.getDescription()).thenReturn("Old desc");

        when(itemRepository.findById(2L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.patchItem(itemDto, 4L))
                .isInstanceOf(ItemAccessDeniedException.class)
                .hasMessageContaining("ACCESS DENIED: you are not an owner of item 2");
    }

    @Test
    void shouldReturnBlankOnEmptyQuery() {
        assertThat(itemService.findItemsByQuery("", 0, 20)).hasSize(0);
    }

    @Test
    void shouldReturnItems() {
        final Item item1 = Item.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .id(1L).build();

        final Item item2 = Item.builder()
                .name("Отвертка")
                .description("Аккумуляторная отвертка")
                .available(true)
                .id(2L).build();

        List<Item> items = List.of(item1, item2);

        when(itemRepository.findItemsByQueryWithPagination(any(), any())).thenReturn(items);

        assertThat(itemService.findItemsByQuery("123", 0, 20)).hasSize(2);
    }
}
