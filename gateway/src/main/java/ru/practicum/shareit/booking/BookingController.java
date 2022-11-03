package ru.practicum.shareit.booking;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.websocket.server.PathParam;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingDto requestDto) {
        log.info("Создаем бронь");
        return bookingClient.createBooking(requestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long bookingId,
                                                @PathParam("approved") @NonNull Boolean approved) {
        log.info("Обновляем бронь");
        return bookingClient.updateBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Получаем бронь по айди");
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookersBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(name = "state",
                                                                defaultValue = "ALL") String stateParam,
                                                        @PositiveOrZero @RequestParam(name = "from",
                                                                defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size",
                                                                defaultValue = "10") Integer size) {
        State state = State.findState(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получаем брони юзера");
        return bookingClient.getAllBookersBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnersBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state",
                                                               defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from",
                                                               defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size",
                                                               defaultValue = "10") Integer size) {
        State state = State.findState(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получаем брони владельца");
        return bookingClient.getAllOwnersBookings(userId, state, from, size);
    }

}
