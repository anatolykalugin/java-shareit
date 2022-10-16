package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @Autowired
    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest() {
        return itemRequestService.createRequest();
    }

    @GetMapping
    public List<ItemRequestDto> getRequests() {
        return itemRequestService.getRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById() {
        return itemRequestService.getRequestById();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOthersRequests() {
        return itemRequestService.getOthersRequests();
    }

}
