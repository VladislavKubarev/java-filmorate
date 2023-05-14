package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> allUsers = new HashMap<>();
    private long actualId = 0;

    public User createUser(User user) {
        if (userLoginValidation(user)) {
            user.setId(++actualId);
            allUsers.put(user.getId(), user);
        }
        return user;
    }

    public List<User> showAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    public User getUserById(long id) {
        if (!allUsers.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с ID %d не существует!", id));
        } else {
            return allUsers.get(id);
        }
    }

    public User updateUser(User newUser) {
        if (!allUsers.containsKey(newUser.getId())) {
            throw new NotFoundException(String.format("Пользователя с ID %d не существует!", newUser.getId()));
        }
        if (userLoginValidation(newUser)) {
            allUsers.put(newUser.getId(), newUser);
        }
        return newUser;
    }

    private boolean userLoginValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return true;
    }
}
