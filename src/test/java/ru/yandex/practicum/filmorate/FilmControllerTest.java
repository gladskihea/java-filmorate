package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private final FilmController controller = new FilmController();

    @Test
    void shouldNotValidateFilmWithEmptyName() {
        Film film = new Film();
        film.setName(""); // Пустое имя
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotValidateFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Имя");
        film.setDescription("a".repeat(201)); // Описание > 200 символов
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotValidateFilmWithOldReleaseDate() {
        Film film = new Film();
        film.setName("Имя");
        film.setReleaseDate(LocalDate.of(1800, 1, 1)); // Слишком старая дата

        assertThrows(ValidationException.class, () -> controller.create(film));
    }
}