package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerId(Long ownerId);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdInThePast(Long ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdInTheFuture(Long ownerId, LocalDateTime date);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND (b.start <= ?2 AND b.end > ?2) " +
            "ORDER BY b.start DESC")
    Collection<Booking> findAllByItemOwnerIdCurrentDate(Long ownerId, LocalDateTime date);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime localDateTime);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime localDateTime);
}
