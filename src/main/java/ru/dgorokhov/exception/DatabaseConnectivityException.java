package ru.dgorokhov.exception;

public class DatabaseConnectivityException extends RuntimeException {

    public DatabaseConnectivityException(String message) {
        super(message);
    }

}
