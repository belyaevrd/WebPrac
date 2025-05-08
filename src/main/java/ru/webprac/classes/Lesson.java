package ru.webprac.classes;

import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Lessons")
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "lesson_id")
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    @NonNull
    private String title;

    @ManyToOne
    @JoinColumn(name = "course", nullable = false)
    @NonNull
    private Course course;

    @Column(name = "begining", columnDefinition = "TIMESTAMP", nullable = false)
    @NonNull
    private LocalDateTime begin;

    @Column(name = "ending", columnDefinition = "TIMESTAMP", nullable = false)
    @NonNull
    private LocalDateTime end;
}