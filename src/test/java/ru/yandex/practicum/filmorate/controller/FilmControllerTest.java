package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    private FilmService filmService;
    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage(), null);
        filmController = new FilmController(filmService);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Film createFilm() {
        Film film = new Film();
        film.setName("Best film in the world");
        film.setDescription("This is the best film in the history of mankind");
        film.setReleaseDate(LocalDate.of(2012, 12, 21));
        film.setDuration(180);
        return film;
    }

    @Test
    void createCorrectFilm() {
        Film testFilm = createFilm();

        filmController.addFilm(testFilm);

        assertEquals(1, testFilm.getId());
        assertEquals(testFilm, filmController.showAllFilms().get(0));
    }

    @Test
    void createFilmWithBlankName() {
        Film testFilm = createFilm();
        testFilm.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);

        assertEquals("Название фильма не должно быть пустым!", violations.iterator().next().getMessage());
    }

    @Test
    void createFilmWithDescriptionWithNumberOfCharactersEqualTo201() {
        Film testFilm = createFilm();
        testFilm.setDescription("Ephraim Winslow arrives on a remote island " +
                "to work as the lighthouse keeper's new assistant. " +
                "His boss is lame and bearded Thomas Wake. " +
                "He treats the assistant like a personal slave and forbids sleeping");

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);

        assertEquals("Описание к фильму не может превышать 200 символов!", violations.iterator().next().getMessage());
    }

    @Test
    void createFilmWithIncorrectReleaseDate() {
        Film testFilm = createFilm();
        testFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.addFilm(testFilm)
        );

        assertEquals("Некорректная дата релиза!", ex.getMessage());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film testFilm = createFilm();
        testFilm.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(testFilm);

        assertEquals("Продолжительность фильма не может быть отрицательной!", violations.iterator().next().getMessage());
    }

    @Test
    void updateNonexistentFilm() {
        Film testFilm = createFilm();
        filmController.addFilm(testFilm);

        Film newFilm = new Film();
        newFilm.setId(10);
        newFilm.setName("Film name");
        newFilm.setDescription("Film description");
        newFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilm.setDuration(120);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> filmController.updateFilm(newFilm)
        );

        assertEquals(String.format("Фильма с ID %d не существует!", newFilm.getId()), ex.getMessage());
    }
}