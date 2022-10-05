package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto mapTo(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment mapFrom(CommentDto commentDto, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                commentDto.getItemId(),
                user,
                commentDto.getCreated()
        );
    }

}
