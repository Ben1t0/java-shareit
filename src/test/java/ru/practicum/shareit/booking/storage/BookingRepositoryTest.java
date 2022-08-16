package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void getAllUserBookings() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Item item2 = Item.builder().name("MEGA drilldzilla!!!!").available(true)
                .description("Mega monster").owner(user2).build();
        em.persist(item2);


        Booking book1User1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item2).build();
        em.persist(book1User1);

        Booking book2User1 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.WAITING).item(item2).build();
        em.persist(book2User1);

        Booking book1User2 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1User2);

        assertThat(bookingRepository.findAllUserBookingsOrderStartDesc(user1.getId(), Pageable.unpaged()))
                .containsExactly(book2User1, book1User1);
    }

    @Test
    void getAllUserBookingsWithStatus() {
        User user1 = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user1);

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllUserBookingsWithStatusOrderStartDesc(user1.getId(), BookingStatus.APPROVED,
                Pageable.unpaged()))
                .containsExactly(book3, book2);
    }

    @Test
    void getAllUserBookingsBeforeDate() {
        User user1 = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user1);

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllUserBookingsBeforeDateOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book1);
    }

    @Test
    void getAllUserBookingsAfterDate() {
        User user1 = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user1);

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllUserBookingsAfterDateOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book3);
    }

    @Test
    void getAllCurrentUserBookings() {
        User user1 = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        em.persist(user1);

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllUserBookingsAtDateOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book2);
    }

    @Test
    void getAllUserItemBookings() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Item item2 = Item.builder().name("MEGA drilldzilla!!!!").available(true)
                .description("Mega monster").owner(user2).build();
        em.persist(item2);


        Booking book1User1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user1).status(BookingStatus.WAITING).item(item2).build();
        em.persist(book1User1);

        Booking book2User1 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user1).status(BookingStatus.WAITING).item(item2).build();
        em.persist(book2User1);

        Booking book1User2 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1User2);

        assertThat(bookingRepository.findAllByItemOwnerIdOrderStartDesc(user2.getId(), Pageable.unpaged()))
                .containsExactly(book2User1, book1User1);
    }

    @Test
    void getAllUserItemBookingsWithStatus() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllByItemOwnerIdAndStatusOrderStartDesc(user1.getId(), BookingStatus.APPROVED,
                Pageable.unpaged()))
                .containsExactly(book3, book2);
    }

    @Test
    void getAllUserItemBookingsBeforeDate() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllByItemOwnerIdInThePastOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book1);
    }

    @Test
    void getAllUserItemBookingsAfterDate() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllByItemOwnerIdInTheFutureOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book3);
    }

    @Test
    void getAllCurrentUserItemBookings() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findAllByItemOwnerIdCurrentDateOrderStartDesc(user1.getId(), now,
                Pageable.unpaged()))
                .containsExactly(book2);
    }

    @Test
    void getLastBookingForItem() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findLastBookingByItemIdOrderEndDesc(item1.getId(), now)).isEqualTo(book1);
    }

    @Test
    void getNextBookingForItem() {
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

        Item item1 = Item.builder().name("Light instrument").available(true)
                .description("drill").owner(user1).build();
        em.persist(item1);

        Booking book1 = Booking.builder().start(now.minusDays(2)).end(now.minusDays(1))
                .booker(user2).status(BookingStatus.WAITING).item(item1).build();
        em.persist(book1);

        Booking book2 = Booking.builder().start(now.minusDays(1)).end(now.plusDays(1))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book2);

        Booking book3 = Booking.builder().start(now.plusDays(1)).end(now.plusDays(2))
                .booker(user2).status(BookingStatus.APPROVED).item(item1).build();
        em.persist(book3);

        assertThat(bookingRepository.findNextBookingByItemIdOrderEndAsc(item1.getId(), now)).isEqualTo(book3);
    }
}