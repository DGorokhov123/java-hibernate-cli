package ru.dgorokhov.exception;

public class EntityValidationException extends RuntimeException {

    public EntityValidationException(String message) {
        super(message);
    }

}
