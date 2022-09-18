package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        return itemServiceImpl.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @RequestBody @Validated({Update.class}) ItemDto itemDto) {
        return itemServiceImpl.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return itemServiceImpl.getItemById(id);
    }

    @GetMapping
    public Set<ItemDto> getUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemServiceImpl.getUsersItems(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@PathVariable Long id) {
        itemServiceImpl.deleteItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        return itemServiceImpl.searchItemsByText(text);
    }
}
