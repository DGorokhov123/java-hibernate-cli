package ru.dgorokhov;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;
import ru.dgorokhov.dal.UserDaoImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoTestcontainersTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static UserDao userDao;

    private final Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        POSTGRES.start();
        System.setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl());
        System.setProperty("hibernate.connection.username", POSTGRES.getUsername());
        System.setProperty("hibernate.connection.password", POSTGRES.getPassword());
        userDao = new UserDaoImpl();
    }

    @AfterAll
    static void afterAll() {
        POSTGRES.stop();
        System.clearProperty("hibernate.connection.url");
        System.clearProperty("hibernate.connection.username");
        System.clearProperty("hibernate.connection.password");
    }

    @Test
    @DisplayName("Создание пользователя")
    void testCreateUser() {
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        Integer age = faker.number().numberBetween(18, 90);

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();
        User savedUser = userDao.create(user);

        assertNotNull(savedUser.getId());
        assertEquals(name, savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(age, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    @DisplayName("Поиск пользователя по ID")
    void testFindById() {
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        Integer age = faker.number().numberBetween(18, 90);

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();
        Long id = userDao.create(user).getId();

        User foundUser = userDao.findById(id);

        assertNotNull(foundUser);
        assertEquals(id, foundUser.getId());
        assertEquals(name, foundUser.getName());
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя по ID")
    void testFindByIdNotFound() {
        User foundUser = userDao.findById(99999L);
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Обновление пользователя")
    void testUpdateUser() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id = userDao.create(user).getId();

        String name2 = faker.name().fullName();
        String email2 = faker.internet().emailAddress();
        Integer age2 = faker.number().numberBetween(18, 90);

        User updateData = User.builder()
                .id(id)
                .name(name2)
                .email(email2)
                .age(age2)
                .build();
        User updatedUser = userDao.update(updateData);

        assertNotNull(updatedUser);
        assertEquals(name2, updatedUser.getName());
        assertEquals(email2, updatedUser.getEmail());
        assertEquals(age2, updatedUser.getAge());
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя")
    void testUpdateUserNotFound() {
        User updateData = User.builder()
                .id(99999L)
                .name("Nonexistent")
                .build();
        User result = userDao.update(updateData);
        assertNull(result);
    }

    @Test
    @DisplayName("Поиск пользователя по Email")
    void testFindByEmail() {
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        Integer age = faker.number().numberBetween(18, 90);
        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();
        userDao.create(user);

        User foundUser = userDao.findByEmail(email);

        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
        assertEquals(name, foundUser.getName());
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя по Email")
    void testFindByEmailNotFound() {
        User foundUser = userDao.findByEmail("nonexistent@example.com");
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Проверка существования пользователя по ID")
    void testExistsById() {
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        Integer age = faker.number().numberBetween(18, 90);
        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();
        Long id = userDao.create(user).getId();

        assertTrue(userDao.existsById(id));
        assertFalse(userDao.existsById(99999L));
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void testFindAll() {
        int initialCount = userDao.findAll().size();

        User user1 = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id1 = userDao.create(user1).getId();

        User user2 = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id2 = userDao.create(user2).getId();

        List<User> allUsers = userDao.findAll();
        assertEquals(initialCount + 2, allUsers.size());

        List<Long> allIds = allUsers.stream()
                .map(User::getId)
                .toList();
        assertTrue(allIds.contains(id1));
        assertTrue(allIds.contains(id2));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void testDeleteUser() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id = userDao.create(user).getId();

        assertTrue(userDao.existsById(id));

        assertTrue(userDao.delete(id));

        assertFalse(userDao.existsById(id));
        assertNull(userDao.findById(id));
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void testDeleteUserNotFound() {
        boolean deleted = userDao.delete(99999L);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Создание пользователя с существующим email")
    void testCreateDuplicateEmailUser() {
        String email = faker.internet().emailAddress();

        User user = User.builder()
                .name(faker.name().fullName())
                .email(email)
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id = userDao.create(user).getId();

        assertTrue(userDao.existsById(id));

        User user2 = User.builder()
                .name(faker.name().fullName())
                .email(email)
                .age(faker.number().numberBetween(18, 90))
                .build();

        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
            userDao.create(user2);
        });
    }

    @Test
    @DisplayName("Обновление пользователя с существующим email")
    void testUpdateDuplicateEmailUser() {
        String email = faker.internet().emailAddress();

        User user1 = User.builder()
                .name(faker.name().fullName())
                .email(email)
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id1 = userDao.create(user1).getId();
        assertTrue(userDao.existsById(id1));

        User user2 = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id2 = userDao.create(user2).getId();
        assertTrue(userDao.existsById(id2));

        User userToUpdate = userDao.findById(id2);
        userToUpdate.setEmail(email);

        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
            userDao.update(userToUpdate);
        });
    }

}
