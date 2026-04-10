package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();

        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        FilmService filmService = new FilmService(filmStorage, userStorage, jdbcTemplate);

        controller = new FilmController(filmStorage, filmService);
    }

    @Test
    void shouldNotValidateFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotValidateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Имя");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotValidateFilmWithOldReleaseDate() {
        Film film = new Film();
        film.setName("Имя");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }
}
