package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenreForFilm(Film film, Set<Genre> genres) {
        String sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";

        List<Genre> genreList = new ArrayList<>(genres);

        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setInt(2, genreList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    @Override
    public void updateGenreForFilm(Film film, Set<Genre> genres) {
        String sqlQuery = "delete from film_genre where film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
        addGenreForFilm(film, genres);
    }

    @Override
    public List<Genre> showAllGenres() {
        String sqlQuery = "select * from genre";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "select * from genre where genre_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } catch (RuntimeException e) {
            throw new NotFoundException(String.format("Жанра с ID %d не существует!", genreId));
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        String sqlQuery = "select * " +
                "from film_genre as fg " +
                "join genre as g on g.genre_id = fg.genre_id " +
                "where fg.film_id = ? " +
                "order by genre_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    @Override
    public void setGenresForFilm(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        String sqlQuery = String.format("select fg.film_id, g.genre_id, g.name, " +
                "from genre g, film_genre fg " +
                "where fg.genre_id = g.genre_id " +
                "and fg.film_id in (%s)", inSql);

        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, identity()));

        jdbcTemplate.query(sqlQuery, (rs) -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("name"));
            film.getGenres().add(genre);
        }, films.stream().map(Film::getId).toArray());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}
