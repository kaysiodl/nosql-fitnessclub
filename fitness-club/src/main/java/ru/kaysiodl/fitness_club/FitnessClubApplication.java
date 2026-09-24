package ru.kaysiodl.fitness_club;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class FitnessClubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnessClubApplication.class, args);
    }


}
