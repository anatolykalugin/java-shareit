package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid RequestDto requestDto) {

        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new ValidationException("Description has to be not empty");
        }

        log.info("Создаем запрос");
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable Long id) {
        log.info("Получаем запрос по айди");
        return itemRequestClient.getItemRequestById(userId, id);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllOwnersRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получаем запросы владельца");
        return itemRequestClient.getAllOwnersRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PositiveOrZero @RequestParam(required = false,
                                                              defaultValue = "0")
                                                      Integer from,
                                                      @Positive @RequestParam(required = false,
                                                              defaultValue = "10")
                                                      Integer size) {
        log.info("Получаем все запросы юзеров");
        return itemRequestClient.getAllUsersRequests(userId, from, size);
    }

}
