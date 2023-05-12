package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> allFilms = new HashMap<>();
    private int actualId = 0;
    private static final LocalDate DATE_BORDER = LocalDate.of(1895, 12, 28);

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        if (releaseDateValidation(film)) {
            film.setId(++actualId);
            allFilms.put(film.getId(), film);
        }
        return film;
    }

    @GetMapping("/films")
    public List<Film> showAllFilms() {
        return new ArrayList<>(allFilms.values());
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        if (!allFilms.containsKey(newFilm.getId())) {
            throw new ValidationException("Такого фильма не существует!");
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
