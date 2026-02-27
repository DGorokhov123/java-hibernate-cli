package ru.dgorokhov;

import ru.dgorokhov.cli.CliOps;
import ru.dgorokhov.dal.HibernateUtil;
import ru.dgorokhov.exception.ExceptionHandler;

import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            HibernateUtil.init();
        } catch (Exception e) {
            System.out.println("Произошла ошибка соединения с базой данных =(");
            return;
        }

        final Scanner scanner = new Scanner(System.in);
        final CliOps cliOps = new CliOps();
        final ExceptionHandler exceptionHandler = new ExceptionHandler();

        while (true) {
            cliOps.showMenu();
            String input = scanner.nextLine().trim();
            if (Objects.equals(input, "0")) break;
            try {
                cliOps.handleCommand(input);
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }

        System.out.println("Завершение работы...");
        HibernateUtil.shutdown();
    }

}
