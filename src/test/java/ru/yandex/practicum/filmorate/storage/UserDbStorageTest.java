package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    private User createUser() {
        User user = new User();
        user.setName("Petr");
        user.setLogin("Petryxa");
        user.setEmail("petr_perviy@yandex.ru");
        user.setBirthday(LocalDate.of(1999, 2, 23));
        return user;
    }

    @Test
    void addUser() {
        User testUser = createUser();
        userDbStorage.createUser(testUser);

        assertEquals(1, testUser.getId());
        assertEquals("Petr", testUser.getName());
        assertEquals("Petryxa", testUser.getLogin());
        assertEquals("petr_perviy@yandex.ru", testUser.getEmail());
        assertEquals("1999-02-23", testUser.getBirthday().toString());
    }

    @Test
    void findUserById() {
        User testUser = createUser();
        userDbStorage.createUser(testUser);

        Optional<User> filmOptional = Optional.of(userDbStorage.getUserById(testUser.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "petr_perviy@yandex.ru")
                );
    }

    @Test
    void showAllUsers() {
        User testUser = createUser();
        userDbStorage.createUser(testUser);
        List<User> usersList = List.of(testUser);

        assertEquals(usersList, userDbStorage.showAllUsers());
    }

    @Test
    void updateUser() {
        User oldUser = createUser();
        userDbStorage.createUser(oldUser);

        oldUser.setName("Vladislav");
        oldUser.setLogin("vLaDiKaVkAz");
        oldUser.setEmail("vlad@yandex.ru");

        User newUser = userDbStorage.updateUser(oldUser);

        assertEquals(newUser, userDbStorage.getUserById(oldUser.getId()));
    }
}
