package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    Collection<Booking> findAllUserBookings(Long bookerId);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    Collection<Booking> findAllUserBookingsWithStatus(Long bookerId, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    Collection<Booking> findAllUserBookingsBeforeDate(Long bookerId, LocalDateTime end);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    Collection<Booking> findAllUserBookingsAfterDate(Long bookerId, LocalDateTime start);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND (b.start >= ?2 AND b.end < ?2) ORDER BY b.start DESC")
    Collection<Booking> findAllUserBookingsAtDate(Long bookerId, LocalDateTime date);

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

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.end < ?2 ORDER BY b.end DESC")
    Booking findLastBookingByItemId(Long itemId, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.end ASC")
    Booking findNextBookingByItemId(Long itemId, LocalDateTime localDateTime);
}
