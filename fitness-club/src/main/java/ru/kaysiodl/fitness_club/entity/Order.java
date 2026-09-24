package ru.kaysiodl.fitness_club.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.kaysiodl.fitness_club.util.OrderStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}