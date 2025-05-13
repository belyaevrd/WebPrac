package ru.webprac;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static ru.webprac.classes.UserRole.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.webprac.DAO.*;
import ru.webprac.classes.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application.properties")
public class DAOTests {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CourseDAO courseDAO;

    @Autowired
    private LessonDAO lessonDAO;

    @Autowired
    private StudentsCoursesDAO studentsCoursesDAO;

    @Autowired
    private TeachersCoursesDAO teachersCoursesDAO;

    @Test
    public void testGetByLogin() {
        User users = userDAO.getByLogin("m.zhukov");
        assertNotNull(users);
        assertEquals("Maksim Zhukov", users.getName());
        assertEquals(Teacher, users.getRole());
        assertEquals("mz1234", users.getPassword());
        User users2 = userDAO.getByLogin("admin");
        assertNull(users2);
    }

    @Test
    public void testGetUsersByRole() {
        Collection<User> teachers = userDAO.getUsersByRole(Teacher);
        assertEquals(3, teachers.size());
        Collection<User> students = userDAO.getUsersByRole(Student);
        assertEquals(3, students.size());
    }

    @Test
    public void testGetTimeTable() {
        User user1 = userDAO.getById(6L);
        Collection<Lesson> set1 = userDAO.getTimetable(user1);
        assertEquals(1, set1.size());

        User user2 = userDAO.getById(1L);
        Collection<Lesson> set2 = userDAO.getTimetable(user2,
                LocalDateTime.parse("2026-05-01T15:00:00"), LocalDateTime.parse("2026-06-01T20:00:00"));
        assertEquals(2, set2.size());
    }

    @Test
    public void testGetStudentsByCourses() {
        Collection<User> students = courseDAO.getStudentsByCourse(3L);
        Collection<Long>Ids = new ArrayList<>();
        for (User user : students) {
            Ids.add(user.getId());
        }
        Collection<Long>expectedIds = new ArrayList<>();
        expectedIds.add(4L);
        expectedIds.add(5L);
        expectedIds.add(6L);
        assertEquals(expectedIds, Ids);
        assertEquals(0, courseDAO.getStudentsByCourse(4L).size());
    }

    @Test
    public void testGetTeachersByCourses() {
        Collection<User> teachers = courseDAO.getTeachersByCourse(1L);
        Collection<Long>Ids = new ArrayList<>();
        for (User user : teachers) {
            Ids.add(user.getId());
        }
        Collection<Long>expectedIds = new ArrayList<>();
        expectedIds.add(1L);
        expectedIds.add(2L);
        expectedIds.add(3L);
        assertEquals(expectedIds, Ids);
        assertEquals(0, courseDAO.getTeachersByCourse(4L).size());
    }

    @Test
    public void testGetLessonsByCourses() {
        Collection<Lesson> set1 = courseDAO.getLessonsByCourse(1L);
        Collection<Lesson> set2 = courseDAO.getLessonsByCourse(2L);
        Collection<Lesson> set3 = courseDAO.getLessonsByCourse(3L);
        Collection<Lesson> set4 = courseDAO.getLessonsByCourse(4L);
        assertEquals(4, set1.size());
        assertEquals(2, set2.size());
        assertEquals(1, set3.size());
        assertEquals(0, set4.size());
    }

    @Test
    public void testGetUserByIdTeachersCourses() {
        User user = userDAO.getById(1L);
        assertEquals(Teacher, user.getRole());
        TeachersCourses tc1 = teachersCoursesDAO.getById(1L, 1L);
        assertNotNull(tc1);
        TeachersCourses tc2 = teachersCoursesDAO.getById(1L, 10L);
        assertNull(tc2);
    }

    @Test
    public void testGetUserByIdStudentsCourses() {
        User user = userDAO.getById(6L);
        assertEquals(Student, user.getRole());
        StudentsCourses sc1 = studentsCoursesDAO.getById(6L, 3L);
        assertNotNull(sc1);
        StudentsCourses sc2 = studentsCoursesDAO.getById(6L, 10L);
        assertNull(sc2);
    }

    @Test
    public void testBaseOperations() {
        User user = new User("admin", "admin", Teacher, "admin");
        assertEquals(6, userDAO.getAll().size());
        userDAO.save(user);
        assertEquals(7, userDAO.getAll().size());

        Long id = user.getId();
        User old_user = userDAO.getById(id);
        assertEquals(old_user.getPassword(), user.getPassword());
        user.setPassword("password");
        userDAO.update(user);
        User new_user = userDAO.getById(id);
        assertNotEquals(old_user.getPassword(), new_user.getPassword());

        userDAO.deleteById(id);
        assertEquals(6, userDAO.getAll().size());

        userDAO.save(user);
        assertEquals(7, userDAO.getAll().size());
        userDAO.delete(user);
        assertEquals(6, userDAO.getAll().size());
    }

    @BeforeEach
    public void init() {
        List<User> users = new ArrayList<>();
        users.add(new User("a.gerasimov", "Artur Gerasimov", Teacher, "ag1234"));
        users.add(new User("m.zhukov",    "Maksim Zhukov", Teacher, "mz1234"));
        users.add(new User("t.kuznecova", "Tatyana Kuznecova", Teacher, "tk1234"));
        users.add(new User("e.alekseeva", "Evgeniya Alekseeva", Student, "ea1234"));
        users.add(new User("a.volkova",   "Alina Volkova", Student, "av1234"));
        users.add(new User("n.egorov",    "Nikolay Egorov", Student, "ne1234"));
        userDAO.saveList(users);

        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Изучение C"));
        courses.add(new Course("Изучение C++"));
        courses.add(new Course("Изучение Java"));
        courses.add(new Course("Изучение Python"));
        courses.add(new Course("Изучение Rust"));
        courseDAO.saveList(courses);

        List<Lesson> lessons = new ArrayList<>();
        lessons.add(new Lesson("Урок 1. Введение", courseDAO.getById(1L),
                LocalDateTime.parse("2026-02-01T15:00:00"), LocalDateTime.parse("2026-02-01T16:00:00")));
        lessons.add(new Lesson("Урок 2. Функции", courseDAO.getById(1L),
                LocalDateTime.parse("2026-02-02T15:00:00"), LocalDateTime.parse("2026-03-01T16:00:00")));
        lessons.add(new Lesson("Урок 3. Структуры данных", courseDAO.getById(1L),
                LocalDateTime.parse("2026-02-03T17:30:00"), LocalDateTime.parse("2026-03-01T18:30:00")));
        lessons.add(new Lesson("Урок 4. Макросы", courseDAO.getById(1L),
                LocalDateTime.parse("2026-02-04T12:00:00"), LocalDateTime.parse("2026-04-01T13:00:00")));
        lessons.add(new Lesson("Урок 5. Библиотеки", courseDAO.getById(3L),
                LocalDateTime.parse("2026-02-05T17:30:00"), LocalDateTime.parse("2026-04-01T18:30:00")));
        lessons.add(new Lesson("Урок 1. Введение", courseDAO.getById(2L),
                LocalDateTime.parse("2026-05-01T18:30:00"), LocalDateTime.parse("2026-05-01T19:30:00")));
        lessons.add(new Lesson("Урок 2. Функции", courseDAO.getById(2L),
                LocalDateTime.parse("2026-06-01T18:30:00"), LocalDateTime.parse("2026-06-01T19:30:00")));
        lessonDAO.saveList(lessons);

        List<TeachersCourses> teachersCourses = new ArrayList<>();
        teachersCourses.add(new TeachersCourses(userDAO.getById(1L), courseDAO.getById(1L)));
        teachersCourses.add(new TeachersCourses(userDAO.getById(1L), courseDAO.getById(2L)));
        teachersCourses.add(new TeachersCourses(userDAO.getById(2L), courseDAO.getById(1L)));
        teachersCourses.add(new TeachersCourses(userDAO.getById(3L), courseDAO.getById(1L)));
        teachersCourses.add(new TeachersCourses(userDAO.getById(2L), courseDAO.getById(3L)));
        teachersCoursesDAO.saveList(teachersCourses);

        List<StudentsCourses> studentsCourses = new ArrayList<>();
        studentsCourses.add(new StudentsCourses(userDAO.getById(4L), courseDAO.getById(2L)));
        studentsCourses.add(new StudentsCourses(userDAO.getById(4L), courseDAO.getById(3L)));
        studentsCourses.add(new StudentsCourses(userDAO.getById(5L), courseDAO.getById(1L)));
        studentsCourses.add(new StudentsCourses(userDAO.getById(5L), courseDAO.getById(3L)));
        studentsCourses.add(new StudentsCourses(userDAO.getById(6L), courseDAO.getById(3L)));
        studentsCoursesDAO.saveList(studentsCourses);
    }

    @BeforeAll
    @AfterEach
    public void cleanUp() {
        userDAO.deleteAllEntries();
        courseDAO.deleteAllEntries();
        lessonDAO.deleteAllEntries();
        studentsCoursesDAO.deleteAllEntries();
        teachersCoursesDAO.deleteAllEntries();
    }
}