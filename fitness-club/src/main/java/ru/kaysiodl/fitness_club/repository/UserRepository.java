package ru.kaysiodl.fitness_club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kaysiodl.fitness_club.entity.Order;
import ru.kaysiodl.fitness_club.entity.Product;
import ru.kaysiodl.fitness_club.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

