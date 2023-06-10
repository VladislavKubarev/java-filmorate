package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenreForFilm(long filmId, int genreId) {
        String sqlQuery = "insert into film_genre (film_id, genre_id) values (?, ?)";

        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public Set<Genre> updateGenreForFilm(Film film, Set<Genre> genres) {
        String sqlQuery = "delete from film_genre where film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());

        film.setGenres(film.getGenres().stream().map(g -> getGenreById(g.getId()))
                .sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toSet()));
        genres.forEach(g -> addGenreForFilm(film.getId(), g.getId()));
        return film.getGenres();
    }

    @Override
    public List<Genre> showAllGenres() {
        String sqlQuery = "select * from genre";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getGenreById(int genreId) {
        String sqlQuery = "select * from genre where genre_id = ?";

        if (doesTheGenreExist(genreId)) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } else {
            throw new NotFoundException(String.format("Жанра с ID %d не существует!", genreId));
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        String sqlQuery = "select *" +
                " from film_genre as fg" +
                " join genre as g on g.genre_id = fg.genre_id" +
                " where fg.film_id = ?" +
                " order by genre_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }

    private boolean doesTheGenreExist(long id) {
        String sqlQuery = "select count(genre_id) from genre where genre_id = ?";
        long countId = jdbcTemplate.queryForObject(sqlQuery, Long.class, id);

        if (countId > 0) {
            return true;
        } else {
            return false;
        }
    }
}
