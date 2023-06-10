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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LikesDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesDbStorage likesDbStorage;

    private Film createFilm1() {
        Film film = new Film();
        film.setName("Best film in the world");
        film.setDescription("This is the best film in the history of mankind");
        film.setReleaseDate(LocalDate.of(2012, 12, 21));
        film.setDuration(180);
        film.setMpa(new Mpa(5, "NC-17"));
        film.setGenres(Set.of(new Genre(1, "Комедия")));
        return film;
    }

    private Film createFilm2() {
        Film film = new Film();
        film.setName("Worst movie in the universe");
        film.setDescription("This is the worst thing I have ever seen");
        film.setReleaseDate(LocalDate.of(2012, 12, 22));
        film.setDuration(90);
        film.setMpa(new Mpa(5, "NC-17"));
        film.setGenres(Set.of(new Genre(2, "Драма"), new Genre(5, "Документальный")));
        return film;
    }

    private User createUser1() {
        User user = new User();
        user.setEmail("vlad@yandex.ru");
        user.setLogin("vLaDiKaVkAz");
        user.setName("Vladislav");
        user.setBirthday(LocalDate.of(1999, 2, 23));
        return user;
    }

    private User createUser2() {
        User user = new User();
        user.setName("Petr");
        user.setLogin("Petryxa");
        user.setEmail("petr_perviy@yandex.ru");
        user.setBirthday(LocalDate.of(1672, 6, 9));
        return user;
    }

    @Test
    void addLikeToFilm() {
        Film film1 = filmDbStorage.addFilm(createFilm1());
        Film film2 = filmDbStorage.addFilm(createFilm2());

        User user1 = userDbStorage.createUser(createUser1());
        User user2 = userDbStorage.createUser(createUser2());

        likesDbStorage.addLike(film1, user1);
        likesDbStorage.addLike(film1, user2);
        likesDbStorage.addLike(film2, user1);

        List<Long> likeListFilm1 = likesDbStorage.getFilmLikes(film1.getId());
        assertEquals(2, likeListFilm1.size());

        List<Long> likeListFilm2 = likesDbStorage.getFilmLikes(film2.getId());
        assertEquals(1, likeListFilm2.size());
    }

    @Test
    void showPopularFilm() {
        Film film1 = filmDbStorage.addFilm(createFilm1());
        Film film2 = filmDbStorage.addFilm(createFilm2());

        User user1 = userDbStorage.createUser(createUser1());
        User user2 = userDbStorage.createUser(createUser2());

        likesDbStorage.addLike(film1, user1);
        likesDbStorage.addLike(film1, user2);
        likesDbStorage.addLike(film2, user1);

        List<Film> popularFilm = likesDbStorage.showPopularFilm(5).stream()
                .map(filmDbStorage::getFilmById).collect(Collectors.toList());

        assertEquals(film1, popularFilm.get(0));  // фильм1 два лайка
        assertEquals(film2, popularFilm.get(1)); // фильм2 1 лайк
    }

    @Test
    void deleteLikesAndShowPopularFilms() {
        Film film1 = filmDbStorage.addFilm(createFilm1());
        Film film2 = filmDbStorage.addFilm(createFilm2());

        User user1 = userDbStorage.createUser(createUser1());
        User user2 = userDbStorage.createUser(createUser2());

        likesDbStorage.addLike(film1, user1);
        likesDbStorage.addLike(film1, user2);
        likesDbStorage.addLike(film2, user1);

        likesDbStorage.deleteLike(film1, user1);
        likesDbStorage.deleteLike(film1, user2);

        List<Film> popularFilm = likesDbStorage.showPopularFilm(5).stream()
                .map(filmDbStorage::getFilmById).collect(Collectors.toList());

        assertEquals(film2, popularFilm.get(0)); // фильм2 1 лайк
        assertEquals(film1, popularFilm.get(1));  // фильм1 ноль лайков
    }
}
