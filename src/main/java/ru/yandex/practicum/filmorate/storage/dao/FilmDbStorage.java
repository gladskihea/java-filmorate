package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.List;

@Component("filmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SQL_INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?";
    private static final String SQL_FIND_ALL = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";
    private static final String SQL_FIND_BY_ID = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE f.film_id = ?";
    private static final String SQL_DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String SQL_INSERT_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_FIND_GENRES = "SELECT g.* FROM genres g JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.genre_id";

    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_FILM, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        updateGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        getById(film.getId());
        jdbcTemplate.update(SQL_UPDATE_FILM, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenres(film);
        return getById(film.getId());
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SQL_FIND_ALL, filmMapper);
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Film getById(Long id) {
        try {
            Film film = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, filmMapper, id);
            loadGenres(film);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film not found");
        }
    }

    private void updateGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_GENRES, film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(SQL_INSERT_GENRES, film.getId(), g.getId());
            }
        }
    }

    private void loadGenres(Film film) {
        List<Genre> genres = jdbcTemplate.query(SQL_FIND_GENRES, (rs, rn) -> new Genre(rs.getInt("genre_id"), rs.getString("name")), film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }
}
