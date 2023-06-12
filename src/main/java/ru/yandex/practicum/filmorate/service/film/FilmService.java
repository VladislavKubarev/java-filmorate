package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesStorage likesStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesStorage likesStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesStorage = likesStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public List<Film> showAllFilms() {
        List<Film> filmList = filmStorage.showAllFilms();

        try {
            filmList.forEach(film -> film.getGenres().addAll(genreStorage.getGenresByFilmId(film.getId())));
        } catch (NullPointerException e) {
            filmList.forEach(film -> film.getGenres().addAll(new ArrayList<>()));
        }

        return filmList;
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);

        genreStorage.getGenresByFilmId(id).forEach(genre -> film.getGenres().add(genre));

        return film;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        likesStorage.addLike(film, user);
    }

    public void deleteLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        likesStorage.deleteLike(film, user);
    }

    public List<Film> showPopularFilm(long count) {
        return likesStorage.showPopularFilm(count).stream().map(filmStorage::getFilmById).collect(Collectors.toList());
    }
}
