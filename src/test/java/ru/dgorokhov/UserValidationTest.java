package ru.dgorokhov;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.dgorokhov.dal.HibernateUtil;
import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;
import ru.dgorokhov.exception.DatabaseConstraintsException;
import ru.dgorokhov.exception.EntityValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private final UserDao userDao = new UserDao();
    private final Faker faker = new Faker();

    @Test
    void validationAnnotationsTest() {

        // 1. Пустое имя (@NotBlank)
        User emptyNameUser = User.builder()
                .name("")
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(emptyNameUser));

        // 2. Null имя (@NotBlank)
        User nullNameUser = User.builder()
                .name(null)
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(nullNameUser));

        // 3. Имя больше 255 символов (@Size)
        String longName = faker.lorem().characters(256);
        User tooLongNameUser = User.builder()
                .name(longName)
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(tooLongNameUser));

        // 4. Пустой email (@NotBlank)
        User emptyEmailUser = User.builder()
                .name(faker.name().fullName())
                .email("")
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(emptyEmailUser));

        // 5. Null email (@NotBlank)
        User nullEmailUser = User.builder()
                .name(faker.name().fullName())
                .email(null)
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(nullEmailUser));

        // 6. Неверный формат email (@Email)
        User invalidEmailUser = User.builder()
                .name(faker.name().fullName())
                .email("not-an-email")
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(invalidEmailUser));

        // 7. Email без @ (@Email)
        User noAtEmailUser = User.builder()
                .name(faker.name().fullName())
                .email("testexample.com")
                .age(25)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(noAtEmailUser));

        // 8. Отрицательный возраст (@Positive)
        User negativeAgeUser = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(-5)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(negativeAgeUser));

        // 9. Нулевой возраст (@Positive)
        User zeroAgeUser = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(0)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(zeroAgeUser));

        // 10. Возраст больше 120 (@Max)
        User tooOldUser = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(121)
                .build();
        assertThrows(EntityValidationException.class, () -> userDao.create(tooOldUser));

    }

    @Test
    void databaseConstraintsTest() {
        String uniqueEmail = faker.internet().emailAddress();
        String name1 = faker.name().fullName();
        String name2 = faker.name().fullName();

        User user1 = User.builder()
                .name(name1)
                .email(uniqueEmail)
                .age(25)
                .build();
        User savedUser1 = assertDoesNotThrow(() -> userDao.create(user1));
        assertNotNull(savedUser1.getId());

        User user2 = User.builder()
                .name(name2)
                .email(uniqueEmail)
                .age(30)
                .build();
        assertThrows(DatabaseConstraintsException.class, () -> userDao.create(user2));

        User foundUser1 = userDao.findById(savedUser1.getId());
        assertNotNull(foundUser1);
        assertEquals(name1, foundUser1.getName());
        assertEquals(uniqueEmail, foundUser1.getEmail());

        userDao.delete(savedUser1.getId());

        User user3 = User.builder()
                .name(name2)
                .email(uniqueEmail)
                .age(30)
                .build();
        User savedUser3 = assertDoesNotThrow(() -> userDao.create(user3));
        assertNotNull(savedUser3.getId());
    }

    @Test
    void emailUniquenessWithUpdateTest() {
        String email1 = faker.internet().emailAddress();
        String email2 = faker.internet().emailAddress();
        String name1 = faker.name().fullName();
        String name2 = faker.name().fullName();

        User user1 = User.builder()
                .name(name1)
                .email(email1)
                .age(25)
                .build();
        User savedUser1 = userDao.create(user1);
        User user2 = User.builder()
                .name(name2)
                .email(email2)
                .age(30)
                .build();
        User savedUser2 = userDao.create(user2);

        savedUser2.setEmail(email1);
        assertThrows(DatabaseConstraintsException.class, () -> userDao.update(savedUser2));

        User unchangedUser2 = userDao.findById(savedUser2.getId());
        assertEquals(email2, unchangedUser2.getEmail());
    }

}
