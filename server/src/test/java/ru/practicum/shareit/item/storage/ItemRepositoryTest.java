package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getAllItemsByOwnerId() {
        User user1 = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user1);

        User user2 = User.builder()
                .email("anotherUser@user.com")
                .name("user")
                .build();

        em.persist(user2);

        Item item1Owner1 = Item.builder().name("drill").available(true)
                .description("Light drill").owner(user1).build();
        em.persist(item1Owner1);
        Item item2Owner1 = Item.builder().name("MEGA drill!!!!").available(true)
                .description("Light drill").owner(user1).build();
        em.persist(item2Owner1);
        Item item1Owner2 = Item.builder().name("Saw").available(true)
                .description("Light drill").owner(user2).build();
        em.persist(item1Owner2);

        assertThat(itemRepository.findAllByOwnerId(user1.getId(), Pageable.unpaged()))
                .containsExactly(item1Owner1, item2Owner1);
    }

    @Test
    void getRightItemBySearchQuery() {
        User user = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user);

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user).build();
        em.persist(item1);
        Item item2 = Item.builder().name("MEGA drilldzilla!!!!").available(true)
                .description("Mega monster").owner(user).build();
        em.persist(item2);
        Item item3 = Item.builder().name("Saw").available(true)
                .description("Saw for wooden").owner(user).build();
        em.persist(item3);

        assertThat(itemRepository.findItemsByQueryWithPagination("drill", Pageable.unpaged()))
                .containsExactly(item1, item2);
    }
}