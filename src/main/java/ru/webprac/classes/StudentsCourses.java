package ru.webprac.classes;

import javax.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "StudentsCourses")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class StudentsCourses implements Serializable {
    @EmbeddedId
    public UserCourse id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @NonNull
    private User student;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    @NonNull
    private Course course;

    public StudentsCourses(User student, Course course) {
        this.student = student;
        this.course = course;
        this.id = new UserCourse(student.getId(), course.getId());
    }
}