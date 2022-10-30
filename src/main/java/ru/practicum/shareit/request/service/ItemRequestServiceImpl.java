package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        log.info("Получен запрос на создание реквеста");
        if (userService.getUserById(requestorId) != null) {
            if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
                User requestor = UserMapper.mapFrom(userService.getUserById(requestorId));
                ItemRequest itemRequest = ItemRequestDtoMapper.mapFrom(itemRequestDto, requestor);
                itemRequest.setCreated(LocalDateTime.now());
                itemRequestRepository.save(itemRequest);
                return ItemRequestDtoMapper.mapTo(itemRequest, null);
            } else {
                log.warn("Ошибка - некорректный запрос");
                throw new ValidationException("Описание запроса не может быть пустым");
            }
        } else {
            log.warn("Ошибка - неправильный айди юзера");
            throw new NotFoundException("Данный пользователь не найден");
        }
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получен запрос на получение реквеста по айди");
        if (userService.getUserById(userId) != null) {
            try {
                ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
                List<Item> items = itemRepository.getByRequestId(itemRequest.getId(),
                        Sort.by("id").descending());
                return ItemRequestDtoMapper.mapTo(itemRequest, items);
            } catch (RuntimeException e) {
                log.warn("Ошибка - отсутствующий запрос");
                throw new NotFoundException("Данный запрос не найден");
            }
        } else {
            log.warn("Ошибка - неправильный айди юзера");
            throw new NotFoundException("Данный пользователь не найден");
        }
    }

    @Override
    public List<ItemRequestDto> getRequests(Long authorId) {
        log.info("Получен запрос на получение реквестов");
        if (userService.getUserById(authorId) != null) {
            Map<Long, List<Item>> requestItemMap = extractItemsToRequests();
            return itemRequestRepository.getByRequestorIdOrderByCreatedAsc(authorId)
                    .stream()
                    .map(itemRequest
                            -> ItemRequestDtoMapper.mapTo(itemRequest, requestItemMap.get(itemRequest.getId())))
                    .collect(Collectors.toList());
        } else {
            log.warn("Ошибка - неправильный айди юзера");
            throw new NotFoundException("Данный юзер не найден");
        }
    }

    @Override
    public List<ItemRequestDto> getOthersRequests(Long userId, int index, int quantity) {
        log.info("Получен запрос на получение всех реквестов");
        if (index >= 0 && quantity > 0) {
            Map<Long, List<Item>> requestItemMap = extractItemsToRequests();
            return itemRequestRepository.getByRequestorIdNot(userId,
                            PageRequest.of(index / quantity, quantity,
                                    Sort.by("created").descending()))
                    .stream()
                    .map(itemRequest
                            -> ItemRequestDtoMapper.mapTo(itemRequest, requestItemMap.get(itemRequest.getId())))
                    .collect(Collectors.toList());
        } else {
            log.warn("Ошибка - некорректный запрос");
            throw new ValidationException("Неправильные вводные");
        }
    }

    private Map<Long, List<Item>> extractItemsToRequests() {
        Map<Long, List<Item>> requestItemMap = new HashMap<>();
        List<Item> itemList = itemRepository.findAll();
        List<ItemRequest> itemRequestList = itemRequestRepository.findAll();
        for (ItemRequest itemRequest : itemRequestList) {
            List<Item> itemsToAdd = new ArrayList<>();
            for (Item item : itemList) {
                if (item.getRequest() != null &&
                        item.getRequest().getId().equals(itemRequest.getId())) {
                    itemsToAdd.add(item);
                }
            }
            requestItemMap.put(itemRequest.getId(), itemsToAdd);
        }
        return requestItemMap;
    }
}
