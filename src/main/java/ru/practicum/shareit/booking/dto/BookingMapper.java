package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

public class BookingMapper {

    public static BookingDto mapTo(Booking booking) {
        if (booking != null) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
        } else {
            return new BookingDto();
        }
    }

    public static Booking mapFrom(BookingDto bookingDto) {
        if (bookingDto != null) {
            return new Booking(
                    bookingDto.getId(),
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    bookingDto.getItem(),
                    bookingDto.getBooker(),
                    bookingDto.getStatus()
            );
        } else {
            return new Booking();
        }
    }
}
