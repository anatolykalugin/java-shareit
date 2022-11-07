package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long bookerId);

    BookingDto updateBooking(Long bookingId, Long ownerId, Boolean isApproved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsForBooker(Long bookerId, String state, int index, int quantity);

    List<BookingDto> getBookingsForOwner(Long ownerId, String state, int index, int quantity);
}
