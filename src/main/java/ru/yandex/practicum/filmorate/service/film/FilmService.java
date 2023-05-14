package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public List<Film> showAllFilms() {
        return filmStorage.showAllFilms();
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        if (filmStorage.getFilmById(filmId) == null || userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
    }

    public void deleteLike(long filmId, long userId) {
        if (filmStorage.getFilmById(filmId) == null || userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Указанный ресурс не найден!");
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> showPopularFilm(long count) {
        List<Film> popularFilm = filmStorage.showAllFilms();
        Collections.sort(popularFilm, new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return film2.getLikes().size() - film1.getLikes().size();
            }
        });
        return popularFilm.stream().limit(count).collect(Collectors.toList());
    }
}
