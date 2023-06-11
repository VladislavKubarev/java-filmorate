package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private long id;
    @NotBlank(message = "Название фильма не должно быть пустым!")
    private String name;
    @Length(max = 200, message = "Описание к фильму не может превышать 200 символов!")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной!")
    private int duration;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();

}
