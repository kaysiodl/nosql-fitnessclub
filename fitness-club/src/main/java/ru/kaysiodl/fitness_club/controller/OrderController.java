package ru.kaysiodl.fitness_club.controller;

import org.springframework.web.bind.annotation.*;
import ru.kaysiodl.fitness_club.entity.Order;
import ru.kaysiodl.fitness_club.entity.Product;
import ru.kaysiodl.fitness_club.entity.User;
import ru.kaysiodl.fitness_club.repository.OrderRepository;
import ru.kaysiodl.fitness_club.repository.ProductRepository;
import ru.kaysiodl.fitness_club.repository.UserRepository;
import ru.kaysiodl.fitness_club.util.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public OrderController(OrderRepository orderRepo, UserRepository userRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @PostMapping
    public Order create(@RequestBody OrderRequest req) {
        User user = userRepo.findById(req.userId()).orElseThrow();
        Product product = productRepo.findById(req.productId()).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);
        return orderRepo.save(order);
    }

    @PatchMapping("/moderate/{id}")
    public Order moderate(@PathVariable Long id, @RequestParam OrderStatus status) {
        Order order = orderRepo.findById(id).orElseThrow();
        order.setStatus(status);
        return orderRepo.save(order);
    }

    @GetMapping
    public List<Order> list() {
        return orderRepo.findAll();
    }
}

