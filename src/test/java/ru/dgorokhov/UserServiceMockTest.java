package ru.dgorokhov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;
import ru.dgorokhov.dto.UserCreateDto;
import ru.dgorokhov.dto.UserResponseDto;
import ru.dgorokhov.dto.UserUpdateDto;
import ru.dgorokhov.service.UserService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceMockTest {

    private final User testUser = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john@example.com")
            .age(30)
            .createdAt(Instant.now())
            .version(0L)
            .build();

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
    }

    // ==================== ПОЗИТИВНЫЕ СЦЕНАРИИ ====================

    @Test
    @DisplayName("Найти всех пользователей - создано 2 штуки")
    void testFindAll_2Users() {
        User testUser2 = User.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@example.com")
                .age(25)
                .createdAt(Instant.now())
                .version(1L)
                .build();
        when(userDao.findAll())
                .thenReturn(List.of(testUser, testUser2));
        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getId(), result.getFirst().getId());
        assertEquals(testUser.getName(), result.getFirst().getName());
        assertEquals(testUser.getEmail(), result.getFirst().getEmail());
        verify(userDao).findAll();
    }

    @Test
    @DisplayName("Найти всех пользователей - пустой лист")
    void testFindAll_EmptyList() {
        when(userDao.findAll())
                .thenReturn(List.of());
        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userDao).findAll();
    }

    @Test
    @DisplayName("Найти пользователя по ID - пользователь найден")
    void testFindById_UserExists() {
        when(userDao.findById(testUser.getId()))
                .thenReturn(testUser);
        UserResponseDto result = userService.findById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getAge(), result.getAge());
        verify(userDao).findById(testUser.getId());
    }

    @Test
    @DisplayName("Найти пользователя по ID - пользователь не найден")
    void testFindById_NotFound() {
        when(userDao.findById(1L))
                .thenReturn(null);
        UserResponseDto result = userService.findById(1L);

        assertNull(result);
        verify(userDao).findById(1L);
    }

    @Test
    @DisplayName("Найти пользователя по Email - пользователь найден")
    void testFindByEmail_UserExists() {
        when(userDao.findByEmail(testUser.getEmail()))
                .thenReturn(testUser);
        UserResponseDto result = userService.findByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userDao).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Найти пользователя по Email - пользователь не найден")
    void testFindByEmail_NotFound() {
        when(userDao.findByEmail(testUser.getEmail()))
                .thenReturn(null);
        UserResponseDto result = userService.findByEmail(testUser.getEmail());

        assertNull(result);
        verify(userDao).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Создание пользователя с корректным DTO")
    void testCreate_ValidDto() {
        when(userDao.create(any(User.class)))
                .thenReturn(testUser);

        UserResponseDto result = userService.create(
                UserCreateDto.builder()
                        .name(testUser.getName())
                        .email(testUser.getEmail())
                        .age(testUser.getAge())
                        .build()
        );

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getAge(), result.getAge());
        verify(userDao).create(any(User.class));
    }

    @Test
    @DisplayName("Создание пользователя с DTO = null")
    void testCreate_NullDto() {
        UserResponseDto result = userService.create(null);

        assertNull(result);
        verify(userDao, never()).create(any());
    }

    @Test
    @DisplayName("Обновление пользователя с корректным DTO")
    void testUpdate_ValidDto() {
        when(userDao.update(any(User.class)))
                .thenReturn(testUser);

        UserResponseDto result = userService.update(
                UserUpdateDto.builder()
                        .id(testUser.getId())
                        .name(testUser.getName())
                        .email(testUser.getEmail())
                        .age(testUser.getAge())
                        .build()
        );

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getAge(), result.getAge());
        verify(userDao).update(any(User.class));
    }

    @Test
    @DisplayName("Обновление пользователя с DTO = null")
    void testUpdate_NullDto() {
        UserResponseDto result = userService.update(null);

        assertNull(result);
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Удаление существующего пользователя")
    void testDelete_UserExists() {
        when(userDao.delete(testUser.getId()))
                .thenReturn(true);
        boolean result = userService.delete(testUser.getId());

        assertTrue(result);
        verify(userDao).delete(testUser.getId());
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void testDelete_NotFound() {
        when(userDao.delete(testUser.getId()))
                .thenReturn(false);
        boolean result = userService.delete(testUser.getId());

        assertFalse(result);
        verify(userDao).delete(testUser.getId());
    }

    @Test
    @DisplayName("Проверка существующего пользователя")
    void testExistsById_UserExists() {
        when(userDao.existsById(testUser.getId()))
                .thenReturn(true);
        boolean result = userService.existsById(testUser.getId());

        assertTrue(result);
        verify(userDao).existsById(testUser.getId());
    }

    @Test
    @DisplayName("Проверка несуществующего пользователя")
    void testExistsById_NotFound() {
        when(userDao.existsById(testUser.getId()))
                .thenReturn(false);
        boolean result = userService.existsById(testUser.getId());

        assertFalse(result);
        verify(userDao).existsById(testUser.getId());
    }

    @Test
    @DisplayName("Создание пользователя - валидация поля Name")
    void testCreateValidation_Name() {
        // Blank
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name("")
                                .email(testUser.getEmail())
                                .age(testUser.getAge())
                                .build()
                )
        );
        // null
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(null)
                                .email(testUser.getEmail())
                                .age(testUser.getAge())
                                .build()
                )
        );
        // too long
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name("a".repeat(256))
                                .email(testUser.getEmail())
                                .age(testUser.getAge())
                                .build()
                )
        );
        verify(userDao, never()).create(any());
    }

    @Test
    @DisplayName("Создание пользователя - валидация поля Email")
    void testCreateValidation_Email() {
        // blank
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email("")
                                .age(testUser.getAge())
                                .build()
                )
        );
        // null
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email(null)
                                .age(testUser.getAge())
                                .build()
                )
        );
        // invalid
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email("not-an-email")
                                .age(testUser.getAge())
                                .build()
                )
        );
        // missing @
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email("testexample.com")
                                .age(testUser.getAge())
                                .build()
                )
        );
        verify(userDao, never()).create(any());
    }

    @Test
    @DisplayName("Создание пользователя - валидация поля Age")
    void testCreateValidation_Age() {
        // negative
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email(testUser.getEmail())
                                .age(-5)
                                .build()
                )
        );
        // zero
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email(testUser.getEmail())
                                .age(0)
                                .build()
                )
        );
        // too large
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.create(
                        UserCreateDto.builder()
                                .name(testUser.getName())
                                .email(testUser.getEmail())
                                .age(121)
                                .build()
                )
        );
        verify(userDao, never()).create(any());
    }

    @Test
    @DisplayName("Обновление пользователя - валидация поля Id")
    void testUpdateValidation_ID() {
        // null
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .name("New Name")
                                .build()
                )
        );
        // negative
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(-1L)
                                .name("New Name")
                                .build()
                )
        );
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Обновление пользователя - валидация поля Name")
    void testUpdateValidation_Name() {
        // too long
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .name("a".repeat(256))
                                .build()
                )
        );
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Обновление пользователя - валидация поля Email")
    void testUpdateValidation_Email() {
        // invalid
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .email("not-an-email")
                                .build()
                )
        );
        // missing @
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .email("testexample.com")
                                .build()
                )
        );
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Обновление пользователя - валидация поля Age")
    void testUpdateValidation_Age() {
        // negative
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .age(-5)
                                .build()
                )
        );
        // zero
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .age(0)
                                .build()
                )
        );
        // too old
        assertThrows(
                jakarta.validation.ConstraintViolationException.class,
                () -> userService.update(
                        UserUpdateDto.builder()
                                .id(testUser.getId())
                                .age(121)
                                .build()
                )
        );
        verify(userDao, never()).update(any());
    }

}
