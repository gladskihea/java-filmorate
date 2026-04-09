package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Component("userDbStorage")
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private static final String SQL_INSERT_USER = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?";
    private static final String SQL_FIND_ALL = "SELECT * FROM users";
    private static final String SQL_FIND_BY_ID = "SELECT * FROM users WHERE user_id=?";

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_USER, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        jdbcTemplate.update(SQL_UPDATE_USER, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, userMapper);
    }

    @Override
    public User getById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("User not found");
        }
    }
}