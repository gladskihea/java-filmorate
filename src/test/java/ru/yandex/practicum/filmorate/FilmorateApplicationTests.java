package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
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
