package ru.kaysiodl.fitness_club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kaysiodl.fitness_club.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
