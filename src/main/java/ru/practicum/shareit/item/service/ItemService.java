package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);
    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);
    ItemDto getItemById(Long id);
    void deleteItemById(Long id);
    Set<ItemDto> getUsersItems(Long userId);
    List<ItemDto> searchItemsByText(String text);
}
