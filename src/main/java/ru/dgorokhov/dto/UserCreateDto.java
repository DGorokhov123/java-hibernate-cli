package ru.dgorokhov.dto;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserCreateDto {

    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    @NotBlank(message = "Field 'name' shouldn't be blank")
    @Size(min = 1, max = 255, message = "Name length should be from 1 to 255 symbols")
    private String name;

    @NotBlank(message = "Field 'email' shouldn't be blank")
    @Size(min = 3, max = 254, message = "Email length should be from 3 to 254 symbols")
    @Email(message = "Field 'email' should match email mask")
    private String email;

    @Positive
    @Max(value = 120, message = "Unfortunately, age can be less then 120 years only")
    private Integer age;

    public void validate() {
        Set<ConstraintViolation<UserCreateDto>> violations = VALIDATOR.validate(this);
        if (!violations.isEmpty()) throw new ConstraintViolationException("UserCreateDto not valid", violations);
    }

}
