package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> allFilms = new HashMap<>();
    private long actualId = 0;
    private static final LocalDate DATE_BORDER = LocalDate.of(1895, 12, 28);

    public Film addFilm(Film film) {
        if (releaseDateValidation(film)) {
            film.setId(++actualId);
            allFilms.put(film.getId(), film);
        }
        return film;
    }

    public List<Film> showAllFilms() {
        return new ArrayList<>(allFilms.values());
    }

    public Film getFilmById(long id) {
        if (!allFilms.containsKey(id)) {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", id));
        } else {
            return allFilms.get(id);
        }
    }

    public Film updateFilm(Film newFilm) {
        if (!allFilms.containsKey(newFilm.getId())) {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", newFilm.getId()));
        }
        if (releaseDateValidation(newFilm)) {
            allFilms.put(newFilm.getId(), newFilm);
        }
        return newFilm;
    }

    private boolean releaseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(DATE_BORDER)) {
            throw new ValidationException("Некорректная дата релиза!");
        } else {
            return true;
        }
    }
}
