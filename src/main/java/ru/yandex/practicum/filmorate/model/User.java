package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id;
    @Email(message = "Введен некорректный email!")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
    @NotBlank(message = "Логин не может быть пустым!")
    private String login;
    private String name;
    @Past(message = "Введена некорректная дата рождения!")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}
