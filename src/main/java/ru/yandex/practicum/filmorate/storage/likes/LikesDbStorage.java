package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Repository
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Film film, User user) {
        String sqlQuery = "insert into likes (film_id, user_id)" +
                "values (?, ?)";

        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ? ";

        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    @Override
    public List<Long> getFilmLikes(long filmId) {
        String sqlQuery = "select user_id from likes where film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, filmId);
    }

    @Override
    public List<Long> showPopularFilm(long count) {
        String sqlQuery = "select f.film_id" +
                " from films as f" +
                " left join likes as l on f.film_id = l.film_id" +
                " group by f.film_id" +
                " order by count(l.user_id) desc" +
                " limit ?";

        return jdbcTemplate.queryForList(sqlQuery, Long.class, count);
    }
}
