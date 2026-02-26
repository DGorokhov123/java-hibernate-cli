package ru.dgorokhov;

import com.github.javafaker.Faker;
import ru.dgorokhov.dal.HibernateUtil;
import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final UserDao userDao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Faker faker = new Faker();

    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory();
        } catch (Exception e) {
            System.out.println("Произошла ошибка соединения с базой данных =(");
            return;
        }

        while (true) {
            showMenu();
            String input = scanner.nextLine().trim();
            if (Objects.equals(input, "0")) {
                System.out.println("Завершение работы...");
                break;
            }
            try {
                handleCommand(input);
            } catch (Exception e) {
                System.out.println("Произошла ошибка:\n" + e.getMessage() + "\nПопробуйте еще раз...");
            }
        }

        HibernateUtil.shutdown();
    }

    private static void handleCommand(String cmd) {
        switch (cmd) {
            case "1" -> showAllUsers();
            case "2" -> addRandomUser();
            case "3" -> addUser();
            case "4" -> updateUser();
            case "5" -> deleteUser();
            default -> System.out.println("Неизвестная команда");
        }
    }

    private static void showAllUsers() {
        List<User> users = userDao.findAll();
        System.out.println("Список всех пользователей:");
        users.forEach(System.out::println);
    }

    private static void addRandomUser() {
        User user = User.builder()
                .name(faker.name().fullName())
                .email(faker.internet().emailAddress())
                .age(faker.number().numberBetween(18, 90))
                .build();
        User savedUser = userDao.create(user);
        System.out.println("Добавлен пользователь: " + savedUser);
    }

    private static void addUser() {
        System.out.print("Введите имя пользователя:\n→ ");
        String inputName = scanner.nextLine().trim();
        System.out.print("Введите email:\n→ ");
        String inputEmail = scanner.nextLine().trim();
        System.out.print("Введите возраст:\n→ ");
        String inputAge = scanner.nextLine().trim();
        int age = 0;
        try {
            age = Integer.parseInt(inputAge);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Age should be integer number, but you have written " + inputAge);
        }
        User user = User.builder()
                .name(inputName)
                .email(inputEmail)
                .age(age)
                .build();
        User savedUser = userDao.create(user);
        System.out.println("Добавлен пользователь: " + savedUser);
    }

    private static void updateUser() {
        System.out.print("Введите ID пользователя:\n→ ");
        String inputId = scanner.nextLine().trim();
        Long id = Long.parseLong(inputId);
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + user);
        System.out.print("Введите новое имя пользователя (Либо просто Enter для сохранения предыдущего):\n→ ");
        String inputName = scanner.nextLine().trim();
        System.out.print("Введите email (Либо просто Enter для сохранения предыдущего):\n→ ");
        String inputEmail = scanner.nextLine().trim();
        System.out.print("Введите возраст (Либо просто Enter для сохранения предыдущего):\n→ ");
        String inputAge = scanner.nextLine().trim();
        if (!inputName.isBlank()) user.setName(inputName);
        if (!inputEmail.isBlank()) user.setEmail(inputEmail);
        if (!inputAge.isBlank()) {
            try {
                user.setAge(Integer.parseInt(inputAge));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Age should be integer number, but you have written " + inputAge);
            }
        }
        userDao.update(user);
        User updatedUser = userDao.findById(id);
        System.out.println("Обновленный пользователь: " + user);
    }

    private static void deleteUser() {
        System.out.print("Введите ID пользователя:\n→ ");
        String inputId = scanner.nextLine().trim();
        Long id = Long.parseLong(inputId);
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        System.out.println("Найден пользователь: " + user);
        userDao.delete(id);
        User deletedUser = userDao.findById(id);
        if (deletedUser == null) {
            System.out.println("Пользователь с ID " + id + " успешно удален.");
        } else {
            System.out.println("Произошла ошибка при удалении пользователя с ID " + id);
        }
    }

    private static void showMenu() {
        System.out.println("\n=== Главное меню (введите 0 для выхода) ===");
        System.out.println(" 1   Показать все записи из таблицы юзеров");
        System.out.println(" 2   Добавить рандомного юзера");
        System.out.println(" 3   Добавить юзера с консоли");
        System.out.println(" 4   Обновить юзера");
        System.out.println(" 5   Удалить юзера");
        System.out.print("→ ");
    }

}
