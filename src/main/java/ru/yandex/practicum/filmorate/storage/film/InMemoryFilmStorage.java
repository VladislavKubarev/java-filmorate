package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> allFilms = new HashMap<>();
    private long actualId = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(++actualId);
        allFilms.put(film.getId(), film);

        return film;
    }

    @Override
    public List<Film> showAllFilms() {
        return new ArrayList<>(allFilms.values());
    }

    @Override
    public Film getFilmById(long id) {
        if (!allFilms.containsKey(id)) {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", id));
        } else {
            return allFilms.get(id);
        }
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (!allFilms.containsKey(newFilm.getId())) {
            throw new NotFoundException(String.format("Фильма с ID %d не существует!", newFilm.getId()));
        }
        allFilms.put(newFilm.getId(), newFilm);

        return newFilm;
    }
}
