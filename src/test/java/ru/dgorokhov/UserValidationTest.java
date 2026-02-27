package ru.dgorokhov;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import ru.dgorokhov.dto.UserCreateDto;
import ru.dgorokhov.dto.UserResponseDto;
import ru.dgorokhov.dto.UserUpdateDto;
import ru.dgorokhov.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private final UserService userService = new UserService();
    private final Faker faker = new Faker();

    @Test
    void createValidationAnnotationsTest() {

        // 1. Пустое имя (@NotBlank)
        UserCreateDto emptyNameUser = UserCreateDto.builder()
                .name("")
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(emptyNameUser));

        // 2. Null имя (@NotBlank)
        UserCreateDto nullNameUser = UserCreateDto.builder()
                .name(null)
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(nullNameUser));

        // 3. Имя больше 255 символов (@Size)
        String longName = faker.lorem().characters(256);
        UserCreateDto tooLongNameUser = UserCreateDto.builder()
                .name(longName)
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(tooLongNameUser));

        // 4. Пустой email (@NotBlank)
        UserCreateDto emptyEmailUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email("")
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(emptyEmailUser));

        // 5. Null email (@NotBlank)
        UserCreateDto nullEmailUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(null)
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(nullEmailUser));

        // 6. Неверный формат email (@Email)
        UserCreateDto invalidEmailUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email("not-an-email")
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(invalidEmailUser));

        // 7. Email без @ (@Email)
        UserCreateDto noAtEmailUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email("testexample.com")
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(noAtEmailUser));

        // 8. Отрицательный возраст (@Positive)
        UserCreateDto negativeAgeUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(-5)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(negativeAgeUser));

        // 9. Нулевой возраст (@Positive)
        UserCreateDto zeroAgeUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(0)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(zeroAgeUser));

        // 10. Возраст больше 120 (@Max)
        UserCreateDto tooOldUser = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(121)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.create(tooOldUser));

    }

    @Test
    void updateValidationAnnotationsTest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        UserResponseDto savedUser = assertDoesNotThrow(() -> userService.create(userCreateDto));
        assertNotNull(savedUser.getId());
        Long id = savedUser.getId();


        // Пустой ID
        UserUpdateDto emptyNameUser = UserUpdateDto.builder()
                .name("Maria")
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(emptyNameUser));

        // Отрицательный ID
        UserUpdateDto nullNameUser = UserUpdateDto.builder()
                .id(-1L)
                .name("Maria")
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(nullNameUser));

        // Имя больше 255 символов (@Size)
        UserUpdateDto tooLongNameUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.lorem().characters(256))
                .email(faker.internet().emailAddress())
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(tooLongNameUser));

        // Неверный формат email (@Email)
        UserUpdateDto invalidEmailUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.name().fullName())
                .email("not-an-email")
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(invalidEmailUser));

        // Email без @ (@Email)
        UserUpdateDto noAtEmailUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.name().fullName())
                .email("testexample.com")
                .age(25)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(noAtEmailUser));

        // Отрицательный возраст (@Positive)
        UserUpdateDto negativeAgeUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(-5)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(negativeAgeUser));

        // Нулевой возраст (@Positive)
        UserUpdateDto zeroAgeUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(0)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(zeroAgeUser));

        // Возраст больше 120 (@Max)
        UserUpdateDto tooOldUser = UserUpdateDto.builder()
                .id(id)
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(121)
                .build();
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userService.update(tooOldUser));

    }

    @Test
    void databaseConstraintsTest() {
        String uniqueEmail = faker.internet().emailAddress();
        String otherEmail = faker.internet().emailAddress();
        String name1 = faker.name().fullName();
        String name2 = faker.name().fullName();
        String name3 = faker.name().fullName();

        UserCreateDto userCreateDto1 = UserCreateDto.builder()
                .name(name1)
                .email(uniqueEmail)
                .age(25)
                .build();
        UserResponseDto savedUser1 = assertDoesNotThrow(() -> userService.create(userCreateDto1));
        assertNotNull(savedUser1.getId());

        UserCreateDto userCreateDto2 = UserCreateDto.builder()
                .name(name2)
                .email(otherEmail)
                .age(52)
                .build();
        UserResponseDto savedUser2 = assertDoesNotThrow(() -> userService.create(userCreateDto2));
        assertNotNull(savedUser2.getId());

        // UserCreateDto

        UserCreateDto userCreateDto3 = UserCreateDto.builder()
                .name(name3)
                .email(uniqueEmail)
                .age(30)
                .build();
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> userService.create(userCreateDto3));

        UserResponseDto foundUser1 = userService.findById(savedUser1.getId());
        assertNotNull(foundUser1);
        assertEquals(name1, foundUser1.getName());
        assertEquals(uniqueEmail, foundUser1.getEmail());

        // UserUpdateDto

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(savedUser2.getId())
                .name(name2)
                .email(uniqueEmail)
                .age(30)
                .build();
        assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> userService.update(userUpdateDto));

        UserResponseDto foundUser2 = userService.findById(savedUser2.getId());
        assertNotNull(foundUser2);
        assertEquals(name2, foundUser2.getName());
        assertEquals(otherEmail, foundUser2.getEmail());

        // Delete and create again

        userService.delete(savedUser1.getId());

        UserCreateDto userCreateDto4 = UserCreateDto.builder()
                .name(name2)
                .email(uniqueEmail)
                .age(30)
                .build();
        UserResponseDto savedUser4 = assertDoesNotThrow(() -> userService.create(userCreateDto4));
        assertNotNull(savedUser4.getId());
    }

}
