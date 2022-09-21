package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper = new ItemMapper();
    private Long id = 1L;

    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item itemToAdd = itemMapper.mapFrom(itemDto);
        itemToAdd.setId(id);
        itemToAdd.setOwner(userId);
        items.put(itemToAdd.getId(), itemToAdd);
        id++;
        itemDto = itemMapper.mapTo(itemToAdd);
        return itemDto;
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (isItemValidForUpdate(userId, itemId)) {
            Item itemToUpdate = itemMapper.mapFrom(itemDto);
            if (itemToUpdate.getName() == null || itemToUpdate.getName().isEmpty()) {
                itemToUpdate.setName(items.get(itemId).getName());
            }
            if (itemToUpdate.getDescription() == null || itemToUpdate.getDescription().isEmpty()) {
                itemToUpdate.setDescription(items.get(itemId).getDescription());
            }
            if (itemToUpdate.getOwner() == null) {
                itemToUpdate.setOwner(items.get(itemId).getOwner());
            }
            if (itemToUpdate.getAvailable() == null) {
                itemToUpdate.setAvailable(items.get(itemId).getAvailable());
            }
            itemToUpdate.setId(itemId);
            items.put(itemId, itemToUpdate);
            itemDto = itemMapper.mapTo(items.get(itemId));
            return itemDto;
        } else {
            throw new NotFoundException("Не пройдена валидация на владельца вещи");
        }
    }

    public ItemDto getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return itemMapper.mapTo(items.get(itemId));
        } else {
            throw new NotFoundException("Не найден предмет с данным id");
        }
    }

    public void deleteItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            items.remove(itemId);
        } else {
            throw new NotFoundException("Не найден предмет с данным id");
        }
    }

    public Set<ItemDto> getUsersItems(Long userId) {
        Set<ItemDto> itemDtos = new HashSet<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                itemDtos.add(itemMapper.mapTo(item));
            }
        }
        return itemDtos;
    }

    public List<ItemDto> searchItemsByText(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<Item> itemsFound = items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .collect(Collectors.toList());
            List<ItemDto> itemDtos = new ArrayList<>();
            for (Item item : itemsFound) {
                itemDtos.add(itemMapper.mapTo(item));
            }
            return itemDtos;
        }
    }

    private boolean isItemValidForUpdate(Long userId, Long itemId) {
        return (getItemById(itemId) != null) && (getItemById(itemId).getOwner().equals(userId));
    }
}
