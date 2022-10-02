package ru.practicum.shareit.item.comment;

public class CommentMapper {

    public static CommentDto mapTo(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getTexts(),
                comment.getItemId(),
                comment.getAuthorId(),
                comment.getCreated()
        );
    }

    public static Comment mapFrom(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getTexts(),
                commentDto.getItemId(),
                commentDto.getAuthorId(),
                commentDto.getCreated()
        );
    }

}
