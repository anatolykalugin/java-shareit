package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.CommentDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private List<CommentDto> commentDtoList;
}
