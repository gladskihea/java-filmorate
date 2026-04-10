package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final String SQL_INSERT_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String SQL_DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String SQL_GET_POPULAR = "SELECT f.film_id FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    public Film create(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        jdbcTemplate.update(SQL_INSERT_LIKE, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.getById(filmId);
        userStorage.getById(userId);
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return jdbcTemplate.query(SQL_GET_POPULAR, (rs, rn) -> filmStorage.getById(rs.getLong("film_id")), count);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Некорректная дата релиза");
        }
    }
}