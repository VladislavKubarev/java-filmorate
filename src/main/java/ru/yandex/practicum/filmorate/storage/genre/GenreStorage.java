package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {

    List<Genre> showAllGenres();

    Genre getGenreById(int genreId);

    List<Genre> getGenresByFilmId(long filmId);

    void addGenreForFilm(Film film, Set<Genre> genres);

    void updateGenreForFilm(Film film, Set<Genre> genres);
}
