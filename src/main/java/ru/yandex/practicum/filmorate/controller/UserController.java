package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private HashMap<Integer, User> allUsers = new HashMap<>();
    private int actualId = 0;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов!");
        } else {
            user.setId(++actualId);
            allUsers.put(user.getId(), user);
            return user;
        }
    }

    @GetMapping("/users")
    public List<User> showAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User newUser) {
        if (!allUsers.containsKey(newUser.getId())) {
            throw new ValidationException("Такого пользователя не существует!");
        } else {
            allUsers.replace(newUser.getId(), newUser);
            return newUser;
        }
    }
}
