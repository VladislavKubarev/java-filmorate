package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    private UserService userService;
    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        userController = new UserController(userService);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private User addTestUser() {
        User user = new User();
        user.setEmail("vlad@yandex.ru");
        user.setLogin("vLaDiKaVkAz");
        user.setName("Vladislav");
        user.setBirthday(LocalDate.of(1999, 2, 23));
        return user;
    }

    @Test
    void createCorrectUser() {
        User testUser = addTestUser();

        userController.createUser(testUser);

        assertEquals(1, testUser.getId());
        assertEquals(testUser, userService.showAllUsers().get(0));
    }

    @Test
    void createUserWithIncorrectEmail() {
        User testUser = addTestUser();
        testUser.setEmail("vlad.yandex.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertEquals("Введен некорректный email!", violations.iterator().next().getMessage());
    }

    @Test
    void createUserWithBlankEmail() {
        User testUser = addTestUser();
        testUser.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertEquals("Email не может быть пустым!", violations.iterator().next().getMessage());
    }

    @Test
    void createUserWithBlankLogin() {
        User testUser = addTestUser();
        testUser.setLogin("");

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertEquals("Логин не может быть пустым!", violations.iterator().next().getMessage());
    }

    @Test
    void createUserWithSpaceInLogin() {
        User testUser = addTestUser();
        testUser.setLogin("vLaD i KaVkAz");

        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.createUser(testUser)
        );

        assertEquals("Логин не может содержать пробелов!", ex.getMessage());
    }

    @Test
    void createUserWithBlankName() {
        User testUser = addTestUser();
        testUser.setName("");
        userController.createUser(testUser);

        assertEquals(testUser.getName(), testUser.getLogin());
    }

    @Test
    void createUserWithIncorrectBirthday() {
        User testUser = addTestUser();
        testUser.setBirthday(LocalDate.of(2030, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(testUser);

        assertEquals("Введена некорректная дата рождения!", violations.iterator().next().getMessage());
    }

    @Test
    void updateNonexistentUser() {
        User testUser = addTestUser();
        userController.createUser(testUser);

        User newUser = new User();
        newUser.setId(10);
        newUser.setName("Petr");
        newUser.setLogin("Petryxa");
        newUser.setEmail("petr_perviy@yandex.ru");
        newUser.setBirthday(LocalDate.of(1672, 6, 9));

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userController.updateUser(newUser)
        );

        assertEquals(String.format("Пользователя с ID %d не существует!", newUser.getId()), ex.getMessage());
    }
}