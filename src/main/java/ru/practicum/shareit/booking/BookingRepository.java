package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBooker_IdOrderByStartPeriodDesc(Long bookerId, Pageable pageable);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartPeriodDesc(Long bookerId, Status status,
                                                                         Pageable pageable);

    @Query("select b from Booking b WHERE b.booker.id = :id and b.startPeriod < :time and b.endPeriod > :time " +
            " order by b.startPeriod desc ")
    List<Booking> findCurrentApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time,
                                                      Pageable pageable);

    @Query("select b from Booking b WHERE b.booker.id = :id and b.startPeriod > :time " +
            "and (upper(b.status) = upper('APPROVED') or upper(b.status) = upper('WAITING')) " +
            "order by b.startPeriod desc ")
    List<Booking> findFutureApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time,
                                                     Pageable pageable);

    @Query("select b from Booking b WHERE b.booker.id = :id and b.endPeriod < :time " +
            "and upper(b.status) = upper('APPROVED') order by b.startPeriod desc ")
    List<Booking> findPastApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time,
                                                   Pageable pageable);

    @Query("select b from Booking b WHERE b.booker.id = :id and b.endPeriod < :time " +
            "and upper(b.status) = upper('APPROVED') order by b.startPeriod desc ")
    List<Booking> findPastApprovedBookingsByBooker(@Param("id") Long bookerId, @Param("time") LocalDateTime time);

    @Query("select b from Booking b join b.item i on b.item.id = i.id where i.owner = :id order by b.startPeriod desc")
    List<Booking> findBookingsByOwner(@Param("id") Long ownerId, Pageable pageable);

    List<Booking> findBookingsByItemOwnerAndStatus(Long ownerId, Status status,
                                                   Pageable pageable);

    @Query("select b from Booking b join b.item i on b.item.id = i.id where i.owner = :id " +
            "and b.startPeriod < :time and b.endPeriod > :time order by b.startPeriod desc")
    List<Booking> findCurrentApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time,
                                                     Pageable pageable);

    @Query("select b from Booking b join b.item i on b.item.id = i.id where i.owner = :id " +
            "and b.startPeriod > :time and (upper(b.status) = upper('APPROVED') or upper(b.status) = upper('WAITING')) " +
            "order by b.startPeriod desc")
    List<Booking> findFutureApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time,
                                                    Pageable pageable);

    @Query("select b from Booking b join b.item i on b.item.id = i.id where i.owner = :id " +
            "and b.endPeriod < :time and upper(b.status) = upper('APPROVED') order by b.startPeriod desc")
    List<Booking> findPastApprovedBookingsByOwner(@Param("id") Long ownerId, @Param("time") LocalDateTime time,
                                                  Pageable pageable);

    Booking findTopByItem_IdAndStartPeriodAfterOrderByStartPeriodAsc(Long itemId, LocalDateTime time);

    Booking findTopByItem_IdAndEndPeriodBeforeOrderByEndPeriodDesc(Long itemId, LocalDateTime time);

}
