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
import java.util.List;
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
        if (userService.getUserById(requestorId) != null) {
            if (itemRequestDto.getDescription() != null && !itemRequestDto.getDescription().isBlank()) {
                User requestor = UserMapper.mapFrom(userService.getUserById(requestorId));
                ItemRequest itemRequest = ItemRequestDtoMapper.mapFrom(itemRequestDto, requestor);
                itemRequest.setCreated(LocalDateTime.now());
                itemRequestRepository.save(itemRequest);
                return ItemRequestDtoMapper.mapTo(itemRequest, null);
            } else {
                throw new ValidationException("Описание запроса не может быть пустым");
            }
        } else {
            throw new NotFoundException("Данный пользователь не найден");
        }
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        if (userService.getUserById(userId) != null) {
            try {
                ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
                List<Item> items = itemRepository.getByRequestId(itemRequest.getId(),
                        Sort.by("id").descending());
                return ItemRequestDtoMapper.mapTo(itemRequest, items);
            } catch (RuntimeException e) {
                throw new NotFoundException("Данный запрос не найден");
            }
        } else {
            throw new NotFoundException("Данный пользователь не найден");
        }
    }

    @Override
    public List<ItemRequestDto> getRequests(Long authorId) {
        if (userService.getUserById(authorId) != null) {
            return itemRequestRepository.getByRequestorIdOrderByCreatedAsc(authorId)
                    .stream()
                    .map(itemRequest -> {
                        List<Item> items = itemRepository.getByRequestId(itemRequest.getId(),
                                Sort.by("id").descending());
                        return ItemRequestDtoMapper.mapTo(itemRequest, items);
                    })
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Данный юзер не найден");
        }
    }

    @Override
    public List<ItemRequestDto> getOthersRequests(Long userId, int index, int quantity) {
        if (index >= 0 && quantity > 0) {
            return itemRequestRepository.getByRequestorIdNot(userId,
                            PageRequest.of(index / quantity, quantity,
                                    Sort.by("created").descending()))
                    .stream()
                    .map(itemRequest -> {
                        List<Item> items = itemRepository.getByRequestId(itemRequest.getId(),
                                Sort.by("id").descending());
                        return ItemRequestDtoMapper.mapTo(itemRequest, items);
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ValidationException("Неправильные вводные");
        }
    }

}
