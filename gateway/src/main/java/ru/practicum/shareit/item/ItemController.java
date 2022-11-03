package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        if (itemDto.getName().isBlank()) {
            throw new ValidationException("Пустое название айтема");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("Пустое описание айтема");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Отсутствует статус доступности айтема");
        }
        log.info("Создаем айтем");
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable Long id) {
        log.info("Обновляем айтем");
        return itemClient.updateItem(itemDto, id, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long id) {
        log.info("Получаем айтем по айди");
        return itemClient.getItemById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получаем все айтемы");
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> showItemsByText(@RequestParam(name = "text", defaultValue = "") String keyword,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Ищем айтем по тексту");
        return itemClient.showItemsByText(keyword, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody CommentDto commentDto) {
        log.info("Добавляем коммент");
        return itemClient.postComment(itemId, userId, commentDto);
    }

}
