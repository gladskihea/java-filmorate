package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.mapper.UserMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        UserMapper userMapper = new UserMapper();

        UserService userService = new UserService(userStorage, jdbcTemplate, userMapper);

        controller = new UserController(userStorage, userService);
    }

    @Test
    void shouldNotValidateUserWithBadEmail() {
        User user = new User();
        user.setEmail("mail-without-at.ru");
        user.setLogin("login");

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldSetLoginAsNameIfNameIsEmpty() {
        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("super_login");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = controller.create(user);

        assertEquals("super_login", createdUser.getName());
    }
}
