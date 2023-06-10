package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    private Film createFilm() {
        Film film = new Film();
        film.setName("Best film in the world");
        film.setDescription("This is the best film in the history of mankind");
        film.setReleaseDate(LocalDate.of(2012, 12, 21));
        film.setDuration(180);
        film.setMpa(new Mpa(5, "NC-17"));
        film.setGenres(Set.of(new Genre(1, "Комедия")));
        return film;
    }

    @Test
    void addFilm() {
        Film testFilm = createFilm();
        filmDbStorage.addFilm(testFilm);

        assertEquals(1, testFilm.getId());
        assertEquals("Best film in the world", testFilm.getName());
        assertEquals(5, testFilm.getMpa().getId());
        assertEquals("NC-17", testFilm.getMpa().getName());
        assertEquals(1, testFilm.getGenres().iterator().next().getId());
        assertEquals("Комедия", testFilm.getGenres().iterator().next().getName());
    }

    @Test
    void findFilmById() {
        Film testFilm = createFilm();
        filmDbStorage.addFilm(testFilm);

        Optional<Film> filmOptional = Optional.of(filmDbStorage.getFilmById(testFilm.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void showAllFilms() {
        Film testFilm = createFilm();
        filmDbStorage.addFilm(testFilm);
        List<Film> filmsList = List.of(testFilm);

        assertEquals(filmsList, filmDbStorage.showAllFilms());
    }

    @Test
    void updateFilm() {
        Film oldFilm = createFilm();
        filmDbStorage.addFilm(oldFilm);

        oldFilm.setName("Worst movie in the universe");
        oldFilm.setDescription("This is the worst thing I have ever seen");
        oldFilm.setGenres(Set.of(new Genre(2, "Драма"), new Genre(5, "Документальный")));

        Film newFilm = filmDbStorage.updateFilm(oldFilm);

        assertEquals(newFilm, filmDbStorage.getFilmById(oldFilm.getId()));
    }
}
