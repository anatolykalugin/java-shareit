package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available")
    private Boolean available;
    @Column(name = "owner")
    private Long owner;
    @JoinColumn(name = "request_id")
    @ManyToOne
    private ItemRequest request;
}
