package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto mapTo(Booking booking) {
        if (booking != null) {
            BookingDto.Item item = new BookingDto.Item();
            if (booking.getItem() != null) {
                item.setId(booking.getItem().getId());
                item.setName(booking.getItem().getName());
            }
            BookingDto.User booker = new BookingDto.User();
            if (booking.getBooker() != null) {
                booker.setId(booking.getBooker().getId());
            }
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                item,
                booker,
                booking.getStatus()
        );
        } else {
            return new BookingDto();
        }
    }

    public static Booking mapFrom(BookingDto bookingDto, User booker, Item item) {
        if (bookingDto != null) {
            return new Booking(
                    bookingDto.getId(),
                    bookingDto.getStart(),
                    bookingDto.getEnd(),
                    item,
                    booker,
                    bookingDto.getStatus()
            );
        } else {
            return new Booking();
        }
    }
}
