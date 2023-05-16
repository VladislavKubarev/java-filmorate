package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> allUsers = new HashMap<>();
    private long actualId = 0;

    @Override
    public User createUser(User user) {
        user.setId(++actualId);
        allUsers.put(user.getId(), user);

        return user;
    }

    @Override
    public List<User> showAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User getUserById(long id) {
        if (!allUsers.containsKey(id)) {
            throw new NotFoundException(String.format("Пользователя с ID %d не существует!", id));
        } else {
            return allUsers.get(id);
        }
    }

    @Override
    public User updateUser(User newUser) {
        if (!allUsers.containsKey(newUser.getId())) {
            throw new NotFoundException(String.format("Пользователя с ID %d не существует!", newUser.getId()));
        }
        allUsers.put(newUser.getId(), newUser);

        return newUser;
    }
}
