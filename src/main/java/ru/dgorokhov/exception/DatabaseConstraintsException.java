package ru.dgorokhov.exception;

public class DatabaseConstraintsException extends RuntimeException {

    public DatabaseConstraintsException(String message) {
        super(message);
    }

}
