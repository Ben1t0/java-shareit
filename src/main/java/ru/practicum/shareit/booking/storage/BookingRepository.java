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
    List<Booking> findAllUserBookingsOrderStartDesc(Long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsWithStatusOrderStartDesc(Long bookerId, BookingStatus bookingStatus,
                                                              Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsBeforeDateOrderStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsAfterDateOrderStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = ?1 AND (b.start < ?2 AND b.end >= ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllUserBookingsAtDateOrderStartDesc(Long bookerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdOrderStartDesc(Long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdAndStatusOrderStartDesc(Long ownerId, BookingStatus bookingStatus,
                                                              Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdInThePastOrderStartDesc(Long ownerId, LocalDateTime date,
                                                              Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdInTheFutureOrderStartDesc(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking as b " +
            "WHERE b.item.id IN (SELECT i.id FROM Item AS i WHERE i.owner.id = ?1) " +
            "AND (b.start <= ?2 AND b.end > ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemOwnerIdCurrentDateOrderStartDesc(Long ownerId, LocalDateTime date, Pageable pageable);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.end < ?2 ORDER BY b.end DESC")
    Booking findLastBookingByItemIdOrderEndDesc(Long itemId, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.end ASC")
    Booking findNextBookingByItemIdOrderEndAsc(Long itemId, LocalDateTime localDateTime);
}
