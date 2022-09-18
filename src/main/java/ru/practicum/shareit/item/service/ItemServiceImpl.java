package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (userRepository.getUserById(userId) != null) {
            log.info("Запрос на добавление вещи");
            return itemRepository.createItem(userId, itemDto);
        } else {
            throw new NotFoundException("Нет такого юзера");
        }
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Запрос на обновление вещи");
        return itemRepository.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Запрос на получение вещи");
        return itemRepository.getItemById(itemId);
    }

    @Override
    public void deleteItemById(Long itemId) {
        log.info("Запрос на удаление вещи");
        itemRepository.deleteItemById(itemId);
    }

    @Override
    public Set<ItemDto> getUsersItems(Long userId) {
        log.info("Запрос на получение вещей юзера");
        return itemRepository.getUsersItems(userId);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        log.info("Запрос на поиск вещей по тексту");
        return itemRepository.searchItemsByText(text);
    }
}
