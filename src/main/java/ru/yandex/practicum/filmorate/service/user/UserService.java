package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public List<User> showAllUsers() {
        return userStorage.showAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User updateUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        friendsStorage.addFriend(user, friend);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        friendsStorage.deleteFriend(user, friend);
    }

    public List<User> showFriendsList(long userId) {
        User user = userStorage.getUserById(userId);

        return friendsStorage.showFriendsList(user.getId());
    }

    public List<User> showCommonFriendsList(long userId, long otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        return friendsStorage.showCommonFriendsList(user.getId(), otherUser.getId());
    }
}
