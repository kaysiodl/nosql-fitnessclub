package ru.kaysiodl.fitness_club.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.kaysiodl.fitness_club.util.Role;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;
}