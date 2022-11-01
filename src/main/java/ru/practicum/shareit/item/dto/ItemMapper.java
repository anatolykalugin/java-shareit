package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto mapTo(Item item, List<CommentDto> commentDtoList) {
        Long requestId = (item.getRequest() != null) ? item.getRequest().getId() : null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                commentDtoList,
                requestId
        );
    }

    public static Item mapFrom(ItemDto itemDto) {
        Item item = new Item();
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            item.setRequest(itemRequest);
        }
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(itemDto.getOwner());
        return item;
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
