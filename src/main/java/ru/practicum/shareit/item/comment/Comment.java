package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "texts")
    private String texts;
    @Column(name = "item")
    private Long itemId;
    @Column(name = "author")
    private Long authorId;
    @Column(name = "created")
    private LocalDateTime created;
}
