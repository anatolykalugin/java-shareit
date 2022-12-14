package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemWithBookingsDto getItemById(Long userId, Long id);

    void deleteItemById(Long id);

    List<ItemWithBookingsDto> getUsersItems(Long userId);

    List<ItemDto> searchItemsByText(String text);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto getSimpleItemById(Long id);
}
