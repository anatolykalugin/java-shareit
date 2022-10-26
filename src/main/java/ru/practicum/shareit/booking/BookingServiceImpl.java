package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        log.info("Запрос на добавление брони");
        if (userService.getUserById(bookerId) != null &&
                itemService.getSimpleItemById(bookingDto.getItemId()) != null) {
            Booking booking = BookingMapper.mapFrom(bookingDto, UserMapper.mapFrom(userService.getUserById(bookerId)),
                    ItemMapper.mapFrom(itemService.getSimpleItemById(bookingDto.getItemId())));
            if (!itemService.getSimpleItemById(bookingDto.getItemId()).getOwner().equals(bookerId)) {
                if (isBookingValid(booking)) {
                    booking.setStatus(Status.WAITING);
                    bookingRepository.save(booking);
                    return BookingMapper.mapTo(booking);
                } else {
                    throw new ValidationException("Не пройдена валидация бронмрования.");
                }
            } else {
                throw new NotFoundException("Нельзя забронировать свою вещь");
            }
        } else {
            throw new NotFoundException("Неправильный юзер или предмет.");
        }
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long ownerId, Boolean isApproved) {
        log.info("Запрос на обновление брони");
        Booking bookingToUpdate = getNotDtoById(bookingId);
        if (!ownerId.equals(bookingToUpdate.getBooker().getId())) {
            if (!bookingToUpdate.getStatus().equals(Status.APPROVED)) {
                if (isApproved) {
                    bookingToUpdate.setStatus(Status.APPROVED);
                } else {
                    bookingToUpdate.setStatus(Status.REJECTED);
                }
                bookingRepository.save(bookingToUpdate);
                return BookingMapper.mapTo(bookingToUpdate);
            } else {
                throw new ValidationException("Бронь уже подтверждена, нельзя отказать");
            }
        } else {
            throw new NotFoundException("Обновить бронь может только владелец");
        }
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Запрос на обновление брони");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронь"));
        if (userService.getUserById(userId) != null &&
                (booking.getBooker().getId().equals(userId) ||
                        itemService.getSimpleItemById(booking.getItem().getId()).getOwner().equals(userId))) {
            return BookingMapper.mapTo(booking);
        } else {
            throw new NotFoundException("Бронь может посмотреть только владелец или автор брони");
        }
    }

    private Booking getNotDtoById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдена бронь"));
    }

    @Override
    public List<BookingDto> getBookingsForBooker(Long bookerId, String state, int index, int quantity) {
        if (index >= 0 && quantity > 0) {
            if (userService.getUserById(bookerId) != null) {
                List<Booking> bookingSet;
                Pageable pageable = PageRequest.of(index / quantity, quantity);
                switch (state) {
                    case "ALL":
                        bookingSet = bookingRepository.findBookingsByBooker_IdOrderByStartPeriodDesc(bookerId,
                                pageable);
                        break;
                    case "CURRENT":
                        bookingSet = bookingRepository.findCurrentApprovedBookingsByBooker(bookerId, LocalDateTime.now(),
                                pageable);
                        break;
                    case "PAST":
                        bookingSet = bookingRepository.findPastApprovedBookingsByBooker(bookerId, LocalDateTime.now(),
                                pageable);
                        break;
                    case "FUTURE":
                        bookingSet = bookingRepository.findFutureApprovedBookingsByBooker(bookerId, LocalDateTime.now(),
                                pageable);
                        break;
                    case "WAITING":
                        bookingSet = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartPeriodDesc(bookerId,
                                Status.WAITING, pageable);
                        break;
                    case "REJECTED":
                        bookingSet = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartPeriodDesc(bookerId,
                                Status.REJECTED, pageable);
                        break;
                    default:
                        throw new ValidationException(String.format("Unknown state: %s", state));
                }
                return bookingSet.stream()
                        .map(BookingMapper::mapTo)
                        .collect(Collectors.toList());
            } else {
                throw new NotFoundException("Юзер не найден");
            }
        } else {
            throw new ValidationException("Неверные вводные");
        }
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state, int index, int quantity) {
        if (index >= 0 && quantity > 0) {
            if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("PAST") && !state.equals("WAITING") &&
                    !state.equals("REJECTED") && !state.equals("FUTURE")) {
                throw new ValidationException(String.format("Unknown state: %s", state));
            } else {
                if (userService.getUserById(ownerId) != null) {
                    List<Booking> bookingSet;
                    Pageable pageable = PageRequest.of(index / quantity, quantity);
                    switch (state) {
                        case "ALL":
                            bookingSet = bookingRepository.findBookingsByOwner(ownerId, pageable);
                            break;
                        case "CURRENT":
                            bookingSet = bookingRepository.findCurrentApprovedBookingsByOwner(ownerId, LocalDateTime.now(),
                                    pageable);
                            break;
                        case "PAST":
                            bookingSet = bookingRepository.findPastApprovedBookingsByOwner(ownerId, LocalDateTime.now(),
                                    pageable);
                            break;
                        case "FUTURE":
                            bookingSet = bookingRepository.findFutureApprovedBookingsByOwner(ownerId, LocalDateTime.now(),
                                    pageable);
                            break;
                        case "WAITING":
                            bookingSet = bookingRepository.findBookingsByItemOwnerAndStatus(ownerId, Status.WAITING,
                                    pageable);
                            break;
                        case "REJECTED":
                            bookingSet = bookingRepository.findBookingsByItemOwnerAndStatus(ownerId, Status.REJECTED,
                                    pageable);
                            break;
                        default:
                            throw new ValidationException(String.format("Unknown state: %s", state));
                    }
                    return bookingSet.stream()
                            .map(BookingMapper::mapTo)
                            .collect(Collectors.toList());
                } else {
                    throw new NotFoundException("Юзер не найден");
                }
            }
        } else {
            throw new ValidationException("Неверные вводные");
        }
    }

    private boolean isBookingValid(Booking booking) {
        return itemService.getSimpleItemById(booking.getItem().getId()).getAvailable() &&
                !booking.getEndPeriod().isBefore(LocalDateTime.now()) &&
                !booking.getStartPeriod().isBefore(LocalDateTime.now()) &&
                !booking.getEndPeriod().isBefore(booking.getStartPeriod());
    }

}
