package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId);

    ItemRequestDto getRequestById(Long userId, Long requestId);

    List<ItemRequestDto> getRequests(Long authorId);

    List<ItemRequestDto> getOthersRequests(Long userId, int index, int quantity);
}
