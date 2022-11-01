package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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
public class BookingServiceTest {
    private final BookingRepository bookingRepository;

    private final BookingService bookingService;

    private final User user1 = new User(1L, "Renat", "renat@mail.ru");
    private final User user2 = new User(2L, "Svetlana", "svetlana@mail.ru");
    private final Item item = new Item(1L, "Ауди", "Не бита, не крашена",
            true, user1.getId(), null);
    private final Item itemNotAvailable = new Item(2L, "БМВ", "Из сервиса не выезжала",
            false, user1.getId(), null);
    private final Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1), item, user2, Status.WAITING);
    private final Booking bookingApproved = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, Status.APPROVED);
    private final Booking bookingRejected = new Booking(3L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            item, user2, Status.REJECTED);

    @Autowired
    public BookingServiceTest(BookingRepository bookingRepository,
                              BookingService bookingService,
                              ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        userService.createUser(UserMapper.mapTo(user1));
        userService.createUser(UserMapper.mapTo(user2));
        itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        bookingRepository.save(booking);
        bookingRepository.save(bookingApproved);
        bookingRepository.save(bookingRejected);
    }

    @Test
    void shouldCreateBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        Booking booking1 = BookingMapper.mapFrom(bookingService.createBooking(bookingDto, 2L), user1, item);
        assertEquals(booking1.getId(), bookingRepository.findById(booking1.getId()).orElse(null).getId());
    }

    @Test
    void shouldFailCreatePastBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        assertThrows(ValidationException.class, () -> BookingMapper
                .mapFrom(bookingService.createBooking(bookingDto, 2L), user1, item));
    }

    @Test
    void shouldFailCreateWrongDatesBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        assertThrows(ValidationException.class, () -> BookingMapper
                .mapFrom(bookingService.createBooking(bookingDto, 2L), user1, item));
    }

    @Test
    void shouldFailCreateTimeCollisionBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().minusDays(2))
                .itemId(1L)
                .build();
        assertThrows(ValidationException.class, () -> BookingMapper
                .mapFrom(bookingService.createBooking(bookingDto, 2L), user1, item));
    }

    @Test
    void shouldFailCreateNotAvailableBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        assertThrows(NotFoundException.class, () -> BookingMapper
                .mapFrom(bookingService.createBooking(bookingDto, 1L), user1, itemNotAvailable));
    }

    @Test
    void shouldFailCreateSelfBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        assertThrows(NotFoundException.class, () -> BookingMapper
                .mapFrom(bookingService.createBooking(bookingDto, 1L), user1, itemNotAvailable));
    }

    @Test
    void shouldUpdateBooking() {
        bookingService.updateBooking(booking.getId(), user1.getId(), true);
        assertEquals(Status.APPROVED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void shouldUpdateAnotherBooking() {
        bookingService.updateBooking(booking.getId(), user1.getId(), false);
        assertEquals(Status.REJECTED, bookingRepository.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void shouldFailUpdateApprovedBooking() {
        assertThrows(ValidationException.class, () -> bookingService
                .updateBooking(bookingApproved.getId(), user1.getId(), true));
    }

    @Test
    void shouldFailUpdateBookingByAnotherUser() {
        assertThrows(NotFoundException.class, () -> bookingService
                .updateBooking(booking.getId(), user2.getId(), true));
    }

    @Test
    void shouldGetBookingById() {
        assertEquals(booking.getId(),
                bookingService.getBookingById(booking.getId(), booking.getBooker().getId()).getId());
    }

    @Test
    void shouldFailGetBookingByIdWrongUser() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 100L));
    }

    @Test
    void shouldGetBookersBookings() {
        assertEquals(3,
                bookingService.getBookingsForBooker(user2.getId(), "ALL", 0, 10).size());
    }

    @Test
    void shouldGetPastBookersBookings() {
        assertEquals(new ArrayList<>(),
                bookingService.getBookingsForBooker(user2.getId(), "PAST", 0, 10));
    }

    @Test
    void shouldGetFutureBookersBookings() {
        assertEquals(new ArrayList<>(),
                bookingService.getBookingsForBooker(user2.getId(), "FUTURE", 0, 10));
    }

    @Test
    void shouldGetCurrentBookersBookings() {
        assertEquals(3,
                bookingService.getBookingsForBooker(user2.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void shouldGetWaitingBookersBookings() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getBookingsForBooker(user2.getId(), "WAITING",
                        0, 10).get(0).getId());
    }

    @Test
    void shouldGetRejectedBookersBookings() {
        assertEquals(List.of(bookingRejected).get(0).getId(),
                bookingService.getBookingsForBooker(user2.getId(), "REJECTED",
                        0, 10).get(0).getId());
    }

    @Test
    void shouldFailGetBookersBookingsNegativeIndex() {
        assertThrows(ValidationException.class, () -> bookingService
                .getBookingsForBooker(user2.getId(), "ALL", -1, -1));
    }

    @Test
    void shouldGetOwnersBookings() {
        assertEquals(3,
                bookingService.getBookingsForOwner(user1.getId(), "ALL", 0, 10).size());
    }

    @Test
    void shouldGetPastOwnersBookings() {
        assertEquals(new ArrayList<>(),
                bookingService.getBookingsForOwner(user1.getId(), "PAST", 0, 10));
    }

    @Test
    void shouldGetFutureOwnersBookings() {
        assertEquals(new ArrayList<>(),
                bookingService.getBookingsForOwner(user1.getId(), "FUTURE", 0, 10));
    }

    @Test
    void shouldGetCurrentOwnersBookings() {
        assertEquals(3,
                bookingService.getBookingsForOwner(user1.getId(), "CURRENT", 0, 10).size());
    }

    @Test
    void shouldGetWaitingOwnersBookings() {
        assertEquals(List.of(booking).get(0).getId(),
                bookingService.getBookingsForOwner(user1.getId(), "WAITING", 0, 10).get(0).getId());
    }

    @Test
    void shouldGetRejectedOwnersBookings() {
        assertEquals(List.of(bookingRejected).get(0).getId(),
                bookingService.getBookingsForOwner(user1.getId(), "REJECTED", 0,
                        10).get(0).getId());
    }

    @Test
    void shouldFailGetOwnersBookingsNegativeIndex() {
        assertThrows(ValidationException.class, () -> bookingService
                .getBookingsForOwner(user1.getId(), "ALL", -1, -1));
    }

    @Test
    void shouldFailGetOwnersBookingsUnknownState() {
        assertThrows(ValidationException.class, () -> bookingService
                .getBookingsForOwner(user1.getId(), "huehuehue", 0, 10));
    }

    @Test
    void shouldFailGetBookersBookingsUnknownState() {
        assertThrows(ValidationException.class, () -> bookingService
                .getBookingsForBooker(user1.getId(), "huehuehue", 0, 10));
    }

    @Test
    void shouldFailGetBookersBookingsWrongUser() {
        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingsForBooker(10L, "ALL", 0, 10));
    }

    @Test
    void shouldFailGetOwnersBookingsWrongUser() {
        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingsForOwner(10L, "ALL", 0, 10));
    }
}
