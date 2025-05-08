package ru.webprac.classes;

import javax.persistence.*;

import com.sun.istack.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "TeachersCourses")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TeachersCourses implements Serializable {
    @EmbeddedId
    public UserCourse id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User teacher;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull
    private Course course;

    public TeachersCourses(User teacher, Course course) {
        this.teacher = teacher;
        this.course = course;
        this.id = new UserCourse(teacher.getId(), course.getId());
    }
}
