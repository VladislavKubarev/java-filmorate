package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private static final LocalDate DATE_BORDER = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        releaseDateValidation(film);
        return filmService.addFilm(film);
    }

    @GetMapping("/films")
    public List<Film> showAllFilms() {
        return filmService.showAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        releaseDateValidation(newFilm);
        return filmService.updateFilm(newFilm);

    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> showPopularFilm(@RequestParam(defaultValue = "10", required = false) long count) {
        return filmService.showPopularFilm(count);
    }

    private void releaseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(DATE_BORDER)) {
            throw new ValidationException("Некорректная дата релиза!");
        }
    }
}
