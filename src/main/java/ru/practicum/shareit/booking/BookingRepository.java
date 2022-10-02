package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerOrderByStartDesc(Long bookerId);

    List<Booking> findBookingsByBookerAndStatusOrderByStartDesc(Long bookerId, Status status);

    @Query("select b from Booking b WHERE b.booker = :id and b.start < :time and b.end > :time " +
            "and upper(b.status) = upper('APPROVED') order by b.start desc ")
    List<Booking> findCurrentApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b WHERE b.booker = :id and b.start > :time " +
            "and upper(b.status) = upper('APPROVED') order by b.start desc ")
    List<Booking> findFutureApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b WHERE b.booker = :id and b.end < :time " +
            "and upper(b.status) = upper('APPROVED') order by b.start desc ")
    List<Booking> findPastApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b join Item i on b.item = i.id where i.owner = :id order by b.start desc")
    List<Booking> findBookingsByOwner(@Param("id") Long ownerId);

    @Query("select b from Booking b join Item i on b.item = i.id where i.owner = :id " +
            "and upper(b.status) = upper(:status) order by b.start desc")
    List<Booking> findBookingsByOwnerAndStatus(@Param("id") Long ownerId, @Param("status") Status status);

    @Query("select b from Booking b join Item i on b.item = i.id where i.owner = :id " +
            "and b.start < :time and b.end > :time and upper(b.status) = upper('APPROVED') order by b.start desc")
    List<Booking> findCurrentApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b join Item i on b.item = i.id where i.owner = :id " +
            "and b.start > :time and upper(b.status) = upper(:status) order by b.start desc")
    List<Booking> findFutureApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b join Item i on b.item = i.id where i.owner = :id " +
            "and b.end < :time and upper(b.status) = upper(:status) order by b.start desc")
    List<Booking> findPastApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time);

    Booking findTopByItemAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    Booking findTopByItemAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

}
