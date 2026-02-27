package ru.dgorokhov.cli;

import com.github.javafaker.Faker;
import ru.dgorokhov.dto.UserCreateDto;
import ru.dgorokhov.dto.UserResponseDto;
import ru.dgorokhov.service.UserService;
import ru.dgorokhov.dto.UserUpdateDto;

import java.util.List;
import java.util.Scanner;

public class CliOps {

    private final UserService userService = new UserService();
    private final Scanner scanner = new Scanner(System.in);
    private final Faker faker = new Faker();

    /*
    Показать главное меню
     */
    public void showMenu() {
        System.out.println("\n=== Главное меню (введите 0 для выхода) ===");
        System.out.println(" 1   Показать все записи из таблицы юзеров");
        System.out.println(" 2   Найти юзера по ID");
        System.out.println(" 3   Найти юзера по Email");
        System.out.println(" 4   Добавить рандомного юзера");
        System.out.println(" 5   Добавить юзера с консоли");
        System.out.println(" 6   Обновить юзера по ID");
        System.out.println(" 7   Удалить юзера по ID");
        System.out.print("→ ");
    }

    /*
    Переключатель по командам
     */
    public void handleCommand(String cmd) {
        switch (cmd) {
            case "1" -> showAllUsers();
            case "2" -> findUserById();
            case "3" -> findUserByEmail();
            case "4" -> addRandomUser();
            case "5" -> addUser();
            case "6" -> updateUser();
            case "7" -> deleteUser();
            default -> System.out.println("Неизвестная команда");
        }
    }

    /*
    Найти пользователя по ID
     */
    private void findUserById() {
        Long id = inputInteger("Введите ID пользователя:").longValue();
        UserResponseDto userResponseDto = userService.findById(id);
        if (userResponseDto == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + userResponseDto);
    }

    /*
    Найти пользователя по Email
     */
    private void findUserByEmail() {
        String email = inputString("Введите Email пользователя:");
        if (email.isBlank()) {
            System.out.println("Email пустой, нечего искать!");
            return;
        }
        UserResponseDto userResponseDto = userService.findByEmail(email);
        if (userResponseDto == null) {
            System.out.println("Пользователь с Email " + email + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + userResponseDto);
    }

    /*
    Показать всех юзеров
     */
    private void showAllUsers() {
        List<UserResponseDto> userResponseDtos = userService.findAll();
        System.out.println("Список всех пользователей:");
        userResponseDtos.forEach(u -> System.out.println(u.toFormattedString()));
    }

    /*
    Добавить случайно сгенерированного юзера
     */
    private void addRandomUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        UserResponseDto userResponseDto = userService.create(userCreateDto);
        System.out.println("Добавлен пользователь: " + userResponseDto);
    }

    /*
    Добавить юзера с консоли
     */
    private void addUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name(inputString("Введите имя пользователя:"))
                .email(inputString("Введите email:"))
                .age(inputInteger("Введите возраст:"))
                .build();
        UserResponseDto userResponseDto = userService.create(userCreateDto);
        System.out.println("Добавлен пользователь: " + userResponseDto);
    }

    /*
    Обновить юзера с консоли
     */
    private void updateUser() {
        Long id = inputInteger("Введите ID пользователя:").longValue();
        UserResponseDto userResponseDto = userService.findById(id);
        if (userResponseDto == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + userResponseDto);
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .id(id)
                .name(inputString("Введите новое имя пользователя (либо Enter для сохранения предыдущего):"))
                .email(inputString("Введите email (либо Enter для сохранения предыдущего):"))
                .age(inputIntegerOrEnter("Введите возраст (либо Enter для сохранения предыдущего):"))
                .build();
        UserResponseDto updatedUserResponseDto = userService.update(userUpdateDto);
        System.out.println("Обновленный пользователь: " + updatedUserResponseDto);
    }

    /*
    Удалить юзера
     */
    private void deleteUser() {
        Long id = inputInteger("Введите ID пользователя:").longValue();
        UserResponseDto userResponseDto = userService.findById(id);
        if (userResponseDto == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + userResponseDto);
        userService.delete(id);
        if (!userService.existsById(id)) System.out.println("Пользователь с ID " + id + " успешно удален.");
    }

    /*
    Функция ввода строки
     */
    private String inputString(String message) {
        System.out.println(message);
        System.out.print("→ ");
        return scanner.nextLine().trim();
    }

    /*
    Функция ввода целого числа
     */
    private Integer inputInteger(String message) {
        return inputInteger(message, false);
    }

    /*
    Функция ввода целого числа или просто Enter
     */
    private Integer inputIntegerOrEnter(String message) {
        return inputInteger(message, true);
    }

    /*
    Функция ввода целого числа кастомная
     */
    private Integer inputInteger(String message, boolean allowEnter) {
        System.out.print(message + "\n→ ");
        while (true) {
            String input = scanner.nextLine().trim();
            if (allowEnter && input.isBlank()) return null;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Ожидается целое число, попробуйте еще раз:\n→ ");
            }
        }
    }

}
