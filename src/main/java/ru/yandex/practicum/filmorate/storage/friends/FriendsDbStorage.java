package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public List<User> showFriendsList(long userId) {
        String sqlQuery = "select * from users where user_id in" +
                "(select friend_id from friends where user_id = ?)";

        return jdbcTemplate.query(sqlQuery, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setBirthday(rs.getDate("birthday").toLocalDate());

                return user;
            }
        }, userId);
    }

    @Override
    public List<User> showCommonFriendsList(long userId, long otherId) {
        String sqlQuery = "select * from users where user_id in" +
                "(select friend_id from friends where user_id = ?)" +
                "and user_id in (select friend_id from friends where user_id = ?)";

        return jdbcTemplate.query(sqlQuery, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setBirthday(rs.getDate("birthday").toLocalDate());

                return user;
            }
        }, userId, otherId);
    }
}
