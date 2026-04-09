package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;

	@Test
	public void testFindUserById() {
		User newUser = new User();
		newUser.setEmail("test@test.ru");
		newUser.setLogin("test");
		newUser.setName("Test User");
		newUser.setBirthday(LocalDate.of(1990, 1, 1));
		userStorage.create(newUser);

		User savedUser = userStorage.getById(1L);

		assertThat(savedUser)
				.isNotNull()
				.hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("email", "test@test.ru");
	}
}
