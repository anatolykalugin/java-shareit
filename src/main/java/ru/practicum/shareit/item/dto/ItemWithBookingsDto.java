package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Booking {
        private Long id;
        private Long bookerId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }

}