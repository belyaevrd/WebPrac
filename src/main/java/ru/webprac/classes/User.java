package ru.webprac.classes;

import lombok.*;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import ru.webprac.util.EnumPSQL;


@Getter
@Setter
@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "login", length = 100, nullable = false)
    @NonNull
    private String login;

    @Column(name = "fullName", length = 100, nullable = false)
    @NonNull
    private String name;

    @Enumerated(EnumType.STRING)
    @Type(EnumPSQL.class)
    @Column(name = "role", nullable = false)
    @NonNull
    private UserRole role;

    @Column(name = "password", length = 100, nullable = false)
    @NonNull
    private String password;
}
