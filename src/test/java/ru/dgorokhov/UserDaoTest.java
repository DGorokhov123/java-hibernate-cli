package ru.dgorokhov;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.dgorokhov.dal.HibernateUtil;
import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private final UserDao userDao = new UserDao();
    private final Faker faker = new Faker();

    @Test
    void addUserTest() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        System.out.println(user);

        User savedUser = userDao.create(user);
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());
        System.out.println(savedUser);

        User foundUser = userDao.findById(savedUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
        System.out.println(foundUser);
    }

    @Test
    void updateUserTest() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id = userDao.create(user).getId();

        User savedUser = userDao.findById(id);
        String newName = faker.name().fullName();
        savedUser.setName(newName);
        userDao.update(savedUser);

        User updatedUser = userDao.findById(id);
        assertEquals(newName, updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    void deleteUserTest() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        Long id = userDao.create(user).getId();

        User savedUser = userDao.findById(id);
        assertNotNull(savedUser);
        assertEquals(user.getName(), savedUser.getName());
        assertEquals(user.getEmail(), savedUser.getEmail());

        userDao.delete(id);
        User deletedUser = userDao.findById(id);
        assertNull(deletedUser);
    }

    @Test
    void findAllUserTest() {
        int num = 10;
        List<User> users = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            User user = User.builder()
                    .name(faker.name().fullName())
                    .email(faker.internet().emailAddress())
                    .age(faker.number().numberBetween(18, 90))
                    .build();
            users.add(userDao.create(user));
        }
        assertEquals(num, users.size());

        Map<Long, User> savedUserMap = userDao.findAll().stream()
                .collect(Collectors.toMap(
                        User::getId,
                        u -> u
                ));

        for (User user : users) {
            assertTrue(savedUserMap.containsKey(user.getId()));
            assertEquals(user.getName(), savedUserMap.get(user.getId()).getName());
            assertEquals(user.getEmail(), savedUserMap.get(user.getId()).getEmail());
        }
    }

}
