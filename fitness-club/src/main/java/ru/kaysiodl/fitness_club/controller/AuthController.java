package ru.kaysiodl.fitness_club.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kaysiodl.fitness_club.entity.User;
import ru.kaysiodl.fitness_club.repository.UserRepository;
import ru.kaysiodl.fitness_club.service.LoginAttemptService;
import ru.kaysiodl.fitness_club.service.OtpService;
import ru.kaysiodl.fitness_club.util.Role;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final OtpService otpService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder, OtpService otpService, LoginAttemptService loginAttemptService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.otpService = otpService;
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username taken");
        }
        User user = new User();
        user.setUsername(req.username());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(req.role() != null ? req.role() : Role.USER);
        userRepo.save(user);

        String code = String.valueOf(100000 + new Random().nextInt(900000));
        try {
            otpService.generateOtp(req.username(), code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("Registered. OTP (dev only): " + code);
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam String username, @RequestParam String code) throws Exception {
        return otpService.verifyOtp(username, code).isPresent() ? ResponseEntity.ok("Confirmed") : ResponseEntity.status(400).body("Invalid or expired code");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
        Optional<User> userOpt = userRepo.findByUsername(req.username());
        if (userOpt.isEmpty() || !encoder.matches(req.password(), userOpt.get().getPasswordHash())) {
            boolean blocked = loginAttemptService.registerFailedAttempt(req.username());
            return ResponseEntity.status(401).body(blocked ? "Blocked: too many attempts" : "Invalid credentials");
        }
        loginAttemptService.resetAttempts(req.username());
        return ResponseEntity.ok("Login OK, role: " + userOpt.get().getRole());
    }
}

