package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.comment.CommentDto;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private Long owner;
    private List<CommentDto> comments;
}
