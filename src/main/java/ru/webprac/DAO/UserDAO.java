package ru.webprac.DAO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ru.webprac.classes.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class UserDAO extends BaseDAO<User, Long> {
    public UserDAO() {
        super(User.class);
    }

    public User getByLogin(@NonNull String login) {
        User result;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Query<User> query = session.createQuery(
                    "from User where login = :login", User.class)
                    .setParameter("login", login);
            result = query.getSingleResult();
        } catch (Exception e) {
            result = null;
        }
        return result;
    }

    public Collection<User> getUsersByRole(UserRole role) {
        Collection<User> result;
        try (Session session = sessionFactory.openSession()) {
            Transaction tr = session.beginTransaction();
            Query<User> query = session.createQuery(
                    "from User where role = :role", User.class)
                    .setParameter("role", role);
            result = query.getResultList();
            tr.commit();
        }
        return result;
    }

    public Collection<Lesson> getTimetable(@NonNull User user, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        Collection<Lesson> result;
        try (Session session = sessionFactory.openSession()) {
            Query<Course> courseQuery;
            Collection<Course> userCourses;
            if (user.getRole() == UserRole.Student) {
                courseQuery = session.createQuery(
                        "select sc.course from StudentsCourses sc where sc.student = :user", Course.class
                ).setParameter("user", user);
            } else {
                courseQuery = session.createQuery(
                        "select tc.course from TeachersCourses tc where tc.teacher = :user", Course.class
                ).setParameter("user", user);
            }
            userCourses = courseQuery.getResultList();
            Query<Lesson> query = session.createQuery(
                    "from Lesson l where l.course in :userCourses" +
                            (end == null ? "" : " and l.begin <= :end") +
                            (start == null ? "" : " and l.end >= :start"),
                    Lesson.class)
                    .setParameter("userCourses", userCourses);
            if (end != null) {
                query.setParameter("end", end);
            }
            if (start != null) {
                query.setParameter("start", start);
            }
            result = query.getResultList();
        }
        return result;
    }

    public Collection<Lesson> getTimetable(@NonNull User user) {
        return getTimetable(user, null, null);
    }
}
