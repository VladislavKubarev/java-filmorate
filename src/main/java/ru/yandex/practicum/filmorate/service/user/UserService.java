package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        if (user == null || friend == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> showFriendsList(long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        List<User> friendsList = new ArrayList<>();
        for (Long idFriend : user.getFriends()) {
            friendsList.add(userStorage.getUserById(idFriend));
        }
        return friendsList;
    }

    public List<User> showCommonFriendsList(long userId, long otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);
        if (user == null || otherUser == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        List<User> commonFriendsList = new ArrayList<>();
        Set<Long> commonId = new HashSet<>(user.getFriends());
        commonId.retainAll(otherUser.getFriends());
        for (Long idFriend : commonId) {
            commonFriendsList.add(userStorage.getUserById(idFriend));
        }
        return commonFriendsList;
    }
}
