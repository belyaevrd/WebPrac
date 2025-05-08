package ru.webprac.DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import ru.webprac.classes.*;

import java.util.Collection;

@Repository
public class CourseDAO extends BaseDAO<Course, Long> {
    public CourseDAO() {
        super(Course.class);
    }

    public Collection<User> getStudentsByCourse(Long courseId) {
        Collection<User> result;
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            Query<User> query = session.createQuery(
                    "select sc.student from StudentsCourses sc where sc.course = :course", User.class)
                    .setParameter("course", this.getById(courseId));
            result = query.getResultList();
            tr.commit();
        }
        return result;
    }

    public Collection<User> getTeachersByCourse(Long courseId) {
        Collection<User> result;
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            Query<User> query = session.createQuery(
                    "select tc.teacher from TeachersCourses tc where tc.course = :course", User.class)
                    .setParameter("course", this.getById(courseId));

            result = query.getResultList();
            tr.commit();
        }
        return result;
    }

    public Collection<Lesson> getLessonsByCourse(Long courseId) {
        Collection<Lesson> result;
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            Query<Lesson> query = session.createQuery(
                    "from Lesson l where l.course = :course", Lesson.class)
                    .setParameter("course", this.getById(courseId));
            result = query.getResultList();
            tr.commit();
        }
        return result;
    }

}