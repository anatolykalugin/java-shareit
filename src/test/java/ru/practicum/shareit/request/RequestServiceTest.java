package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class RequestServiceTest {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestService itemRequestService;
    private final User user = new User(1L, "Anastasia", "anastasia@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "New phone", user, LocalDateTime.now());

    @Autowired
    public RequestServiceTest(ItemRequestRepository itemRequestRepository, ItemRequestService itemRequestService,
                              UserService userService) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestService = itemRequestService;
        userService.createUser(UserMapper.mapTo(user));
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void shouldCreateRequest() {
        ItemRequestDto itemRequest1 = itemRequestService
                .createRequest(ItemRequestDtoMapper.mapTo(itemRequest, new ArrayList<>()),
                        itemRequest.getRequestor().getId());
        assertEquals(ItemRequestDtoMapper.mapTo(itemRequestRepository
                .findById(itemRequest1.getId()).orElseThrow(), new ArrayList<>()).getId(), itemRequest1.getId());
    }

    @Test
    void shouldGetRequestById() {
        assertEquals(itemRequestService.getRequestById(itemRequest.getId(),
                user.getId()).getId(), ItemRequestDtoMapper.mapTo(itemRequest, new ArrayList<>()).getId());
    }

    @Test
    void shouldGetAllRequests() {
        assertEquals(itemRequestService.getRequests(user.getId()).get(0).getId(),
                List.of(ItemRequestDtoMapper.mapTo(itemRequest, new ArrayList<>())).get(0).getId());
    }

    @Test
    void shouldGetOthersRequests() {
        assertEquals(itemRequestService.getOthersRequests(2L, 0, 10).get(0).getId(),
                List.of(ItemRequestDtoMapper.mapTo(itemRequest, new ArrayList<>())).get(0).getId());
    }

    @Test
    void shouldFailGetOthersRequestsNegativeIndex() {
        assertThrows(ValidationException.class, () -> itemRequestService
                .getOthersRequests(1L, -1, 10));
    }
}
