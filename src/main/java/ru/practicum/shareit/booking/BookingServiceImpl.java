package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;

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
                itemService.getItemById(bookingDto.getId()) != null) {
            Booking booking = BookingMapper.mapFrom(bookingDto);
            if (isBookingValid(booking)) {
                booking.setStatus(Status.WAITING);
                bookingRepository.save(booking);
                return BookingMapper.mapTo(booking);
            } else {
                throw new ValidationException("Не пройдена валидация бронмрования.");
            }
        } else {
            throw new NotFoundException("Неправильный юзер или предмет.");
        }
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long ownerId, Boolean isApproved) {
        log.info("Запрос на обновление брони");
        Booking bookingToUpdate = BookingMapper.mapFrom(getBookingById(bookingId, ownerId));
        if (isApproved) {
            bookingToUpdate.setStatus(Status.APPROVED);
        } else {
            bookingToUpdate.setStatus(Status.REJECTED);
        }
        bookingRepository.save(bookingToUpdate);
        return BookingMapper.mapTo(bookingToUpdate);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        log.info("Запрос на обновление брони");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдена бронь"));
        if (userService.getUserById(userId) != null &&
                (booking.getBooker().equals(userId) ||
                        itemService.getItemById(booking.getItem()).getOwner().equals(userId))) {
            return BookingMapper.mapTo(booking);
        } else {
            throw new ValidationException("Бронь может посмотреть только владелец или автор брони");
        }
    }

    @Override
    public List<BookingDto> getBookingsForBooker(Long bookerId, String state) {
        if (userService.getUserById(bookerId) != null) {
            List<Booking> bookingList;
            switch (state) {
                case "ALL":
                    bookingList = bookingRepository.findBookingsByBookerOrderByStartDesc(bookerId);
                    break;
                case "CURRENT":
                    bookingList = bookingRepository.findCurrentApprovedBookingsByBooker(bookerId, LocalDateTime.now());
                    break;
                case "PAST":
                    bookingList = bookingRepository.findPastApprovedBookingsByBooker(bookerId, LocalDateTime.now());
                    break;
                case "FUTURE":
                    bookingList = bookingRepository.findFutureApprovedBookingsByBooker(bookerId, LocalDateTime.now());
                    break;
                case "WAITING":
                    bookingList = bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(bookerId,
                            Status.WAITING);
                    break;
                case "REJECTED":
                    bookingList = bookingRepository.findBookingsByBookerAndStatusOrderByStartDesc(bookerId,
                            Status.REJECTED);
                    break;
                default:
                    throw new ValidationException("Некорректный запрос");
            }
            return bookingList.stream()
                    .map(BookingMapper::mapTo)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Юзер не найден");
        }
    }

    @Override
    public List<BookingDto> getBookingsForOwner(Long ownerId, String state) {
        if (userService.getUserById(ownerId) != null) {
            List<Booking> bookingList;
            switch (state) {
                case "ALL":
                    bookingList = bookingRepository.findBookingsByOwner(ownerId);
                    break;
                case "CURRENT":
                    bookingList = bookingRepository.findCurrentApprovedBookingsByOwner(ownerId, LocalDateTime.now());
                    break;
                case "PAST":
                    bookingList = bookingRepository.findPastApprovedBookingsByOwner(ownerId, LocalDateTime.now());
                    break;
                case "FUTURE":
                    bookingList = bookingRepository.findFutureApprovedBookingsByOwner(ownerId, LocalDateTime.now());
                    break;
                case "WAITING":
                    bookingList = bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.WAITING);
                    break;
                case "REJECTED":
                    bookingList = bookingRepository.findBookingsByOwnerAndStatus(ownerId, Status.REJECTED);
                    break;
                default:
                    throw new ValidationException("Некорректный запрос");
            }
            return bookingList.stream()
                    .map(BookingMapper::mapTo)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Юзер не найден");
        }
    }

    private boolean isBookingValid(Booking booking) {
        return !booking.getEnd().isBefore(LocalDateTime.now()) &&
                !booking.getStart().isBefore(LocalDateTime.now()) &&
                !booking.getEnd().isBefore(booking.getStart());
    }

}
