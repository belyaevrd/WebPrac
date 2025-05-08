package ru.webprac.DAO;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.webprac.classes.TeachersCourses;
import ru.webprac.classes.UserCourse;

@Repository
public class TeachersCoursesDAO extends BaseDAO<TeachersCourses, UserCourse> {
    public TeachersCoursesDAO() {
        super(TeachersCourses.class);
    }

    public TeachersCourses getById(Long user_id, Long course_id) {
        TeachersCourses teachersCourses;
        try (Session session = sessionFactory.openSession()) {
            UserCourse id = new UserCourse(user_id, course_id);
            teachersCourses = session.get(entityClass, id);
        }
        return teachersCourses;
    }

}
