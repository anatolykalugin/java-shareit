package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long booker;
    private Status status;
}
