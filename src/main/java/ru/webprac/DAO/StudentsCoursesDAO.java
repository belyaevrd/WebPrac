package ru.webprac.DAO;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.webprac.classes.StudentsCourses;
import ru.webprac.classes.UserCourse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class StudentsCoursesDAO extends BaseDAO<StudentsCourses, UserCourse> {
    public StudentsCoursesDAO() {
        super(StudentsCourses.class);
    }

    public StudentsCourses getById(Long user_id, Long course_id) {
        StudentsCourses studentsCourses;
        try (Session session = sessionFactory.openSession()) {
            UserCourse id = new UserCourse(user_id, course_id);
            studentsCourses = session.get(entityClass, id);
        }
        return studentsCourses;
    }

}
