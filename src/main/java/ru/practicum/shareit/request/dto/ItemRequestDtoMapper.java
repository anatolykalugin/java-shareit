package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestDtoMapper {

    public static ItemRequestDto mapTo(ItemRequest itemRequest) {
        ItemRequestDto.User user = new ItemRequestDto.User(itemRequest.getId());
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                user
        );
    }

    public static ItemRequest mapFrom(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor
        );
    }
}
