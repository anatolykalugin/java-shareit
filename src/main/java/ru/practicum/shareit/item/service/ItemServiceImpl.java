package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Запрос на добавление вещи");
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Нет такого юзера");
        }
        Item item = ItemMapper.mapFrom(itemDto);
        item.setOwner(userId);
        if (isItemValid(item)) {
            if (item.getRequest() != null) {
                ItemRequest itemRequest = itemRequestRepository.findById(item.getRequest().getId())
                        .orElseThrow(() -> new NotFoundException("Реквест не найден"));
                item.setRequest(itemRequest);
            }
            itemRepository.save(item);
            return ItemMapper.mapTo(item, getItemsCommentsDtos(item.getId()));
        } else {
            throw new ValidationException("Не пройдена валидация предмета.");
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        Item itemToUpdate = validateItemForUpdate(itemId, userId, ItemMapper.mapFrom(itemDto));
        itemRepository.save(itemToUpdate);
        return ItemMapper.mapTo(itemToUpdate, getItemsCommentsDtos(itemToUpdate.getId()));
    }

    @Override
    public ItemWithBookingsDto getItemById(Long userId, Long itemId) {
        log.info("Запрос на получение вещи");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет такого предмета"));
        if (item.getOwner().equals(userId)) {
            return ItemMapper.mapToWithBookings(item,
                    bookingRepository.findTopByItem_IdAndEndPeriodBeforeOrderByEndPeriodDesc(item.getId(),
                            LocalDateTime.now()),
                    bookingRepository.findTopByItem_IdAndStartPeriodAfterOrderByStartPeriodAsc(item.getId(),
                            LocalDateTime.now()),
                    commentRepository.findByItemIdOrderByCreatedDesc(item.getId()));
        } else {
            return ItemMapper.mapToWithBookings(item, null, null,
                    commentRepository.findByItemIdOrderByCreatedDesc(item.getId()));
        }
    }

    @Override
    public ItemDto getSimpleItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Нет такого предмета"));
        return ItemMapper.mapTo(item, getItemsCommentsDtos(itemId));
    }

    @Override
    @Transactional
    public void deleteItemById(Long itemId) {
        log.info("Запрос на удаление вещи");
        try {
            itemRepository.deleteById(itemId);
        } catch (RuntimeException e) {
            throw new NotFoundException("Не найден такой предмет");
        }
    }

    @Override
    public List<ItemWithBookingsDto> getUsersItems(Long userId) {
        log.info("Запрос на получение вещей юзера");
        if (userService.getUserById(userId) != null) {
            List<Item> itemSet = new ArrayList<>(itemRepository.getItemsByOwnerOrderByIdAsc(userId));
            List<ItemWithBookingsDto> listToShow = new ArrayList<>();
            for (Item i : itemSet) {
                ItemWithBookingsDto itemToAdd = ItemMapper.mapToWithBookings(i,
                        bookingRepository.findTopByItem_IdAndEndPeriodBeforeOrderByEndPeriodDesc(i.getId(),
                                LocalDateTime.now()),
                        bookingRepository.findTopByItem_IdAndStartPeriodAfterOrderByStartPeriodAsc(i.getId(),
                                LocalDateTime.now()),
                        commentRepository.findByItemIdOrderByCreatedDesc(i.getId()));
                listToShow.add(itemToAdd);
            }
            return listToShow;
        } else {
            throw new NotFoundException("Не найден такой юзер");
        }
    }

    @Override
    @Transactional
    public List<ItemDto> searchItemsByText(String text) {
        log.info("Запрос на поиск вещей по тексту");
        if (!text.isBlank()) {
            Collection<Item> itemList = itemRepository.searchItemsByText(text
                    .toLowerCase());
            List<ItemDto> itemDtos = new ArrayList<>();
            for (Item i : itemList) {
                itemDtos.add(ItemMapper.mapTo(i, getItemsCommentsDtos(i.getId())));
            }
            return itemDtos;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Запрос на добавление комментария");
        if (isCommentValid(userId, itemId, commentDto.getText())) {
            Comment comment = CommentMapper.mapFrom(commentDto, ItemMapper.mapFrom(getSimpleItemById(itemId)),
                    UserMapper.mapFrom(userService.getUserById(userId)));
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Комментарий добавлен.");
            return CommentMapper.mapTo(comment);
        } else {
            throw new ValidationException("Не пройдена валидация комментария (аренда вещи не найдена)");
        }
    }

    private List<CommentDto> getItemsCommentsDtos(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(CommentMapper::mapTo)
                .collect(Collectors.toList());
    }

    private boolean isItemValid(Item item) {
        return !item.getName().isBlank() && !item.getDescription().isBlank() && item.getAvailable() != null;
    }

    private Item validateItemForUpdate(Long itemId, Long userId, Item item) {
        if (userService.getUserById(userId) != null && getSimpleItemById(itemId).getOwner().equals(userId)) {
            Item itemToUpdate = ItemMapper.mapFrom(getSimpleItemById(itemId));
            itemToUpdate.setOwner(userId);
            if (item.getName() != null && !item.getName().isBlank()) {
                itemToUpdate.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                itemToUpdate.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemToUpdate.setAvailable(item.getAvailable());
            }
            return itemToUpdate;
        } else {
            throw new NotFoundException("Не найден такой юзер");
        }
    }

    private boolean isCommentValid(Long userId, Long itemId, String text) {
        boolean isValid = false;
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Не найден предмет");
        }
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException("Не найден юзер");
        }
        if (text == null || text.isBlank()) {
            throw new ValidationException("Текст комментария отсутствует");
        }
        List<Booking> bookings = bookingRepository.findPastApprovedBookingsByBooker(userId, LocalDateTime.now());
        for (Booking b : bookings) {
            if (b.getItem().getId().equals(itemId)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }
}
