package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.Mapper;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper implements Mapper<ItemDto, Item> {

    @Override
    public ItemDto mapTo(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner()
        );
    }

    @Override
    public Item mapFrom(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner()
        );
    }
}
