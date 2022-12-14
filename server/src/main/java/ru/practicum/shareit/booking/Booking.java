package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime startPeriod;
    @Column(name = "end_date")
    private LocalDateTime endPeriod;
    @JoinColumn(name = "item_id", nullable = false)
    @ManyToOne
    private Item item;
    @JoinColumn(name = "booker_id", nullable = false)
    @ManyToOne
    private User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
}
