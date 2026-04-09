package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String SQL_INSERT_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_GET_FRIENDS = "SELECT u.* FROM users u JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?";
    private static final String SQL_GET_COMMON_FRIENDS = "SELECT u.* FROM users u " +
            "JOIN friends f1 ON u.user_id = f1.friend_id " +
            "JOIN friends f2 ON u.user_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    private final UserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public User create(User user) {
        validateName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.getById(user.getId());
        validateName(user);
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
        jdbcTemplate.update(SQL_INSERT_FRIEND, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.getById(userId);
        userStorage.getById(friendId);
        jdbcTemplate.update(SQL_DELETE_FRIEND, userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.getById(userId);
        return jdbcTemplate.query(SQL_GET_FRIENDS, userMapper, userId);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        userStorage.getById(id);
        userStorage.getById(otherId);
        return jdbcTemplate.query(SQL_GET_COMMON_FRIENDS, userMapper, id, otherId);
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}