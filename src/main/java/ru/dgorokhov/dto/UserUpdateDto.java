package ru.dgorokhov.dto;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserUpdateDto {

    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    @NotNull(message = "UserUpdateDto should have ID ")
    @Positive(message = "ID should be positive number")
    private Long id;

    @Size(max = 255, message = "Name length should be from 1 to 255 symbols")
    private String name;

    @Size(max = 254, message = "Email length should be from 3 to 254 symbols")
    @Email(message = "Field 'email' should match email mask")
    private String email;

    @Positive
    @Max(value = 120, message = "Unfortunately, age can be less then 120 years only")
    private Integer age;

    public void validate() {
        Set<ConstraintViolation<UserUpdateDto>> violations = VALIDATOR.validate(this);
        if (!violations.isEmpty()) throw new ConstraintViolationException("UserUpdateDto not valid", violations);
    }

}
