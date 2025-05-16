package ru.webprac.classes;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "Courses")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "course_id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    @NonNull
    private String name;

    @Column(name = "description")
    private String description;
}