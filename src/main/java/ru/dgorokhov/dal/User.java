package ru.dgorokhov.dal;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Field 'name' shouldn't be blank")
    @Size(min = 1, max = 255, message = "Name length should be from 1 to 255 symbols")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Field 'email' shouldn't be blank")
    @Size(min = 3, max = 254, message = "Email length should be from 3 to 254 symbols")
    @Email(message = "Field 'email' should match email mask")
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Positive
    @Max(value = 120, message = "Unfortunately, age can be less then 120 years only")
    @Column(name = "age")
    private Integer age;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' +
                ", age=" + age + ", createdAt=" + createdAt + ", version=" + version + '}';
    }

}
