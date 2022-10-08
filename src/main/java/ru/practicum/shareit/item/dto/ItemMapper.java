package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapTo(Item item, List<CommentDto> commentDtoList) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                commentDtoList
        );
    }

    public static Item mapFrom(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner()
        );
    }

    public static ItemWithBookingsDto mapToWithBookings(Item item, Booking lastBooking,
                                                        Booking nextBooking, List<Comment> commentList) {
        ItemWithBookingsDto.Booking lastBooking1 = new ItemWithBookingsDto.Booking();
        if (lastBooking != null) {
            lastBooking1.setId(lastBooking.getId());
            lastBooking1.setBookerId(lastBooking.getBooker().getId());
        } else {
            lastBooking1 = null;
        }
        ItemWithBookingsDto.Booking nextBooking1 = new ItemWithBookingsDto.Booking();
        if (nextBooking != null) {
            nextBooking1.setId(nextBooking.getId());
            nextBooking1.setBookerId(nextBooking.getBooker().getId());
        } else {
            nextBooking1 = null;
        }
        List<ItemWithBookingsDto.Comment> comments = commentList.stream()
                .map(comment -> new ItemWithBookingsDto.Comment(comment.getId(),
                        comment.getText(), comment.getAuthor().getName(),
                        comment.getCreated())).collect(Collectors.toList());
        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking1,
                nextBooking1,
                comments
        );
    }

}
