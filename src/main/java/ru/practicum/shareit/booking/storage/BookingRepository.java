package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus bookingStatus);

    Collection<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    Collection<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start,
                                                                                   LocalDateTime end);

    Collection<Booking> findAllByItemIdInOrderByStartDesc(Collection<Long> itemsId);

    Collection<Booking> findAllByItemIdInAndStatusOrderByStartDesc(Collection<Long> itemsId,
                                                                   BookingStatus bookingStatus);

    Collection<Booking> findAllByItemIdInAndEndIsBeforeOrderByStartDesc(Collection<Long> itemsId, LocalDateTime end);

    Collection<Booking> findAllByItemIdInAndStartAfterOrderByStartDesc(Collection<Long> itemsId, LocalDateTime start);

    Collection<Booking> findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(Collection<Long> itemsId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end);

    Collection<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime localDateTime);

    Collection<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(Long itemId, LocalDateTime localDateTime);
}
