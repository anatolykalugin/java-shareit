package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    List<CommentDto> comments;
    Long requestId;
}
