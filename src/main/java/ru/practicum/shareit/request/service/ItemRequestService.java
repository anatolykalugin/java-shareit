package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest();

    ItemRequestDto getRequestById();

    List<ItemRequestDto> getRequests();

    List<ItemRequestDto> getOthersRequests();
}
