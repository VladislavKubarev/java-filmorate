package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    List<Long> showFriendsList(User user);

}
