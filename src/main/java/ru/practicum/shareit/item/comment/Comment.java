package ru.practicum.shareit.item.comment;

import lombok.*;
import ru.practicum.shareit.user.model.User;

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
    @Column(name = "text")
    private String text;
    @Column(name = "item")
    private Long itemId;
    @JoinColumn(name = "author", nullable = false)
    @ManyToOne
    private User author;
    @Column(name = "created")
    private LocalDateTime created;
}
