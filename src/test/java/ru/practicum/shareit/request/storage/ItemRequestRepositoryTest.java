package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void returnRequestsByRequesterId() {
        User requester = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(requester);

        ItemRequest itemRequestPast = ItemRequest.builder()
                .requester(requester)
                .description("need drill")
                .created(LocalDateTime.now().minusDays(1))
                .build();

        ItemRequest itemRequestFuture = ItemRequest.builder()
                .requester(requester)
                .description("need drill")
                .created(LocalDateTime.now().plusDays(1))
                .build();


        em.persist(itemRequestPast);
        em.persist(itemRequestFuture);


        assertThat(itemRequestRepository.findAllByRequesterIdOrderByCreatedAsc(requester.getId()))
                .hasSize(2)
                .containsExactly(itemRequestPast, itemRequestFuture);
    }


    @Test
    void returnRequestsByNotRequesterId() {
        User requester = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(requester);

        User anotherUser = User.builder()
                .email("anotherUser@user.com")
                .name("user")
                .build();
        em.persist(anotherUser);

        ItemRequest itemRequest = ItemRequest.builder()
                .requester(requester)
                .description("need drill")
                .created(LocalDateTime.now())
                .build();

        ItemRequest itemRequestAnotherUser = ItemRequest.builder()
                .requester(anotherUser)
                .description("need MEGA drill!!!")
                .created(LocalDateTime.now())
                .build();

        em.persist(itemRequest);
        em.persist(itemRequestAnotherUser);

        assertThat(itemRequestRepository.findAllByRequesterIdNot(requester.getId(), Pageable.unpaged()))
                .hasSize(1)
                .containsExactly(itemRequestAnotherUser);
    }
}