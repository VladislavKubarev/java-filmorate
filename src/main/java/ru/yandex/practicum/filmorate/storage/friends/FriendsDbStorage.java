package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Repository
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(User user, User friend) {
        String sqlQuery = "insert into friends (user_id, friend_id)" +
                "values (?, ?)";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        String sqlQuery = "delete from friends where user_id = ? and friend_id = ?";

        jdbcTemplate.update(sqlQuery, user.getId(), friend.getId());
    }

    @Override
    public List<Long> showFriendsList(User user) {
        String sqlQuery = "select friend_id from friends where user_id = ?";

        return jdbcTemplate.queryForList(sqlQuery, Long.class, user.getId());
    }
}
