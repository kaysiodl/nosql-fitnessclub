package ru.kaysiodl.fitness_club.controller;

import ru.kaysiodl.fitness_club.util.Role;

record RegisterRequest(String username, String password, Role role) {
}

