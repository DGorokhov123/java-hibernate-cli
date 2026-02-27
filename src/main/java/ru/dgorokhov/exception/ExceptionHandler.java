package ru.dgorokhov.exception;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
public class ExceptionHandler {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BRIGHT_RED = "\u001B[91m";

    /*
    Обработчик-переключатель по типу ошибок
     */
    public void handleException(Exception e) {
        System.out.print(RED + "Произошла ошибка:\n" + BRIGHT_RED);
        switch (e) {
            case jakarta.validation.ConstraintViolationException ex -> handleSpecifiedException(ex);
            case org.hibernate.exception.ConstraintViolationException ex -> handleSpecifiedException(ex);
            case org.hibernate.exception.JDBCConnectionException ex -> handleSpecifiedException(ex);
            default -> handleUnknownException(e);
        }
        System.out.println(RESET + "Попробуйте еще раз...");
    }

    /*
    Обработка ошибок валидации DTO
     */
    private void handleSpecifiedException(jakarta.validation.ConstraintViolationException e) {
        log.debug("Validation Error", e);
        String violations = e.getConstraintViolations().stream()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        System.out.println(e.getMessage() + ": " + violations);
    }

    /*
    Обработка ошибок целостности БД
     */
    private void handleSpecifiedException(org.hibernate.exception.ConstraintViolationException e) {
        log.debug("Database Constraints Error", e);
        System.out.print("Нарушено ограничение " + e.getKind().name());
        Throwable rootCause = org.hibernate.internal.util.ExceptionHelper.getRootCause(e);
        String rootMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();
        if (rootMessage != null) {
            String[] split = rootMessage.split("\n");
            if (split.length > 0) {
                System.out.println(": " + split[split.length - 1]);
            }
        }
    }

    /*
    Обработка ошибок соединения с БД
     */
    private void handleSpecifiedException(org.hibernate.exception.JDBCConnectionException e) {
        log.error("Database connection error", e);
        System.out.println("Database connection error" + e.getMessage());
    }

    /*
    Обработка всех прочих ошибок
     */
    private void handleUnknownException(Exception e) {
        log.debug("Unknown error", e);
        System.out.println("Unknown error" + e.getMessage());
    }

}
