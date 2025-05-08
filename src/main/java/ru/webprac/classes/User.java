package ru.webprac.classes;

import lombok.*;
import org.hibernate.annotations.Type;
import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
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
    @Type(type = "ru.webprac.util.EnumPSQL")
    @Column(name = "role", nullable = false)
    @NonNull
    private UserRole role;

    @Column(name = "password", length = 100, nullable = false)
    @NonNull
    private String password;
}
