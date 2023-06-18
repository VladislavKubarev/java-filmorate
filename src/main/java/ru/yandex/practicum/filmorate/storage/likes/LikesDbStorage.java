package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public List<Film> showPopularFilm(long count) {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "rm.mpa_id, rm.name as mpa_name, " +
                "from films as f " +
                "left join rating_mpa as rm on f.mpa_id = rm.mpa_id " +
                "left join likes as l on f.film_id = l.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";

        return jdbcTemplate.query(sqlQuery, new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = new Film();
                film.setId(rs.getLong("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));

                return film;
            }
        }, count);
    }
}
