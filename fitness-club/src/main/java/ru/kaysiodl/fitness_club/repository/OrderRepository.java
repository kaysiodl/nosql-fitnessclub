package ru.kaysiodl.fitness_club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kaysiodl.fitness_club.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
