package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> findAllUserBookings(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsWithStatus(Long bookerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsBeforeDate(Long bookerId, LocalDateTime end, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsAfterDate(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = ?1 AND (b.start < ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsAtDate(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdInThePast(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdInTheFuture(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND (b.start <= ?2 AND b.end > ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdCurrentDate(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.end < ?2 ORDER BY b.end DESC")
    Booking findLastBookingByItemId(Long itemId, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.end ASC")
    Booking findNextBookingByItemId(Long itemId, LocalDateTime localDateTime);
}
