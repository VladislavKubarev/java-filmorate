package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface LikesStorage {

    void addLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> showPopularFilm(long count);

    List<Long> getFilmLikes(long filmId);
}
