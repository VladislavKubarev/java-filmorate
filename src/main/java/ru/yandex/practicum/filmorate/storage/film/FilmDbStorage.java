package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.*;

import java.util.List;

@Primary
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into films (name, description, release_date, duration, mpa_id)" +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        genreStorage.addGenreForFilm(film, film.getGenres());

        return film;
    }

    @Override
    public List<Film> showAllFilms() {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "rm.mpa_id, rm.name as mpa_name " +
                "from films as f " +
                "join rating_mpa as rm on f.mpa_id = rm.mpa_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";

        if (doesTheFilmExist(newFilm.getId())) {
            jdbcTemplate.update(sqlQuery, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                    newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());

            genreStorage.updateGenreForFilm(newFilm, newFilm.getGenres());

            return newFilm;
        } else {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", newFilm.getId()));
        }
    }

    @Override
    public Film getFilmById(long id) {
        String sqlQuery = "select f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "rm.mpa_id, rm.name as mpa_name " +
                "from films as f " +
                "join rating_mpa as rm on f.mpa_id = rm.mpa_id " +
                "where f.film_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (RuntimeException e) {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", id));
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("film_id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(new Mpa(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")));

        return film;
    }

    private boolean doesTheFilmExist(long id) {
        String sqlQuery = "select count(film_id) from films where film_id = ?";
        long countId = jdbcTemplate.queryForObject(sqlQuery, Long.class, id);

        if (countId > 0) {
            return true;
        } else {
            return false;
        }
    }
}

