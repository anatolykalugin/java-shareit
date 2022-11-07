package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestDtoMapper {

    public static ItemRequestDto mapTo(ItemRequest itemRequest, List<Item> items) {
        List<ItemRequestDto.ItemDto> itemDtosList = new ArrayList<>();
        if (items != null) {
            itemDtosList = items.stream()
                    .map(item -> new ItemRequestDto.ItemDto(item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            item.getRequest().getId()))
                    .collect(Collectors.toList());
        }
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemDtosList
        );
    }

    public static ItemRequest mapFrom(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                itemRequestDto.getCreated()
        );
    }
}
