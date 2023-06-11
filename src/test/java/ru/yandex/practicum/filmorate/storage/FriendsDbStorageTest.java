package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FriendsDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FriendsDbStorage friendsDbStorage;

    private User createUser1() {
        User user = new User();
        user.setEmail("vlad@yandex.ru");
        user.setLogin("vLaDiKaVkAz");
        user.setName("Vladislav");
        user.setBirthday(LocalDate.of(1999, 2, 23));
        return user;
    }

    private User createUser2() {
        User user = new User();
        user.setName("Petr");
        user.setLogin("Petryxa");
        user.setEmail("petr_perviy@yandex.ru");
        user.setBirthday(LocalDate.of(1985, 6, 9));
        return user;
    }

    @Test
    void addAsFriends() {
        User user1 = userDbStorage.createUser(createUser1());
        User user2 = userDbStorage.createUser(createUser2());

        friendsDbStorage.addFriend(user1, user2);
        friendsDbStorage.addFriend(user2, user1);

        List<User> friendsByUser1 = friendsDbStorage.showFriendsList(user1.getId()); // Лист ID друзей юзера1
        List<User> friendsByUser2 = friendsDbStorage.showFriendsList(user2.getId()); // Лист ID друзей юзера2

        assertTrue(friendsByUser1.contains(user2));
        assertTrue(friendsByUser2.contains(user1));
    }

    @Test
    void deleteAsFriends() {
        User user1 = userDbStorage.createUser(createUser1());
        User user2 = userDbStorage.createUser(createUser2());

        friendsDbStorage.addFriend(user1, user2);
        friendsDbStorage.addFriend(user2, user1);

        friendsDbStorage.deleteFriend(user1, user2);

        List<User> FriendsByUser1 = friendsDbStorage.showFriendsList(user1.getId()); // Лист ID друзей юзера1

        assertEquals(0, FriendsByUser1.size());
    }
}
