package ru.kaysiodl.fitness_club.controller;

import org.springframework.web.bind.annotation.*;
import ru.kaysiodl.fitness_club.entity.Product;
import ru.kaysiodl.fitness_club.repository.ProductRepository;
import ru.kaysiodl.fitness_club.service.ProductCacheService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepo;
    private final ProductCacheService cacheService;

    public ProductController(ProductRepository productRepo, ProductCacheService cacheService) {
        this.productRepo = productRepo;
        this.cacheService = cacheService;
    }

    @GetMapping
    public List<Product> list() throws Exception {
        return cacheService.getProducts(productRepo::findAll);
    }

    @PostMapping
    public Product create(@RequestBody Product product) throws Exception {
        Product saved = productRepo.save(product);
        cacheService.invalidate();
        return saved;
    }
}