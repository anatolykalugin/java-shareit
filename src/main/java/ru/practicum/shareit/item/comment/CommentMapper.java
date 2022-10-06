package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto mapTo(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment mapFrom(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated()
        );
    }

}
