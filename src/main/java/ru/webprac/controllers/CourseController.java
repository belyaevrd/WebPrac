package ru.webprac.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.*;
import ru.webprac.classes.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.webprac.classes.UserRole.Student;
import static ru.webprac.classes.UserRole.Teacher;

@Controller
public class CourseController {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    private LessonDAO lessonDAO;
    @Autowired
    private TeachersCoursesDAO teachersCoursesDAO;
    @Autowired
    private StudentsCoursesDAO studentsCoursesDAO;

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseDAO.getAll());
        return "courses";
    }

    @GetMapping("/courses/add")
    public String addCourse(RedirectAttributes redirectAttributes,
                            HttpSession session)
    {
        if (session.getAttribute("user") == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Добавить новый курс могут только авторизованные пользователи!");
            return "redirect:/courses";
        }
        UserRole userRole = ((User) session.getAttribute("user")).getRole();
        if (userRole != Teacher) {
            redirectAttributes.addFlashAttribute("error",
                    "Добавить новый курс могут только преподаватели!");
            return "redirect:/courses";
        }
        return "addCourse";
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam String name,
                            @RequestParam(required = false) String description,
                            RedirectAttributes redirectAttributes,
                            HttpSession session,
                            Model model)
    {
        Course course = new Course(name);
        if (description != null) {
            course.setDescription(description);
        }
        try {
            courseDAO.save(course);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
        model.addAttribute("course", course);
        return "redirect:/courses/" + course.getId();
    }

    private boolean manageRools(Long courseId,
                             HttpSession session,
                             Model model)
    {
        boolean result = false;

        if (session.getAttribute("user") == null) {
            model.addAttribute("manageRools", result);
            return result;
        }

        User user = (User) session.getAttribute("user");

        if (user.getRole() == Teacher) {
            Collection<Long> Ids = new ArrayList<Long>();
            Ids = courseDAO.getTeachersByCourse(courseId)
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            result = Ids.contains(user.getId());
        }
        model.addAttribute("manageRools", result);
        return result;
    }

    @GetMapping("/courses/{id}")
    public String course(@PathVariable Long id,
                         RedirectAttributes redirectAttributes,
                         HttpSession session,
                         Model model)
    {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }
        Collection<User> teachers = courseDAO.getTeachersByCourse(id);
        Collection<User> students = courseDAO.getStudentsByCourse(id);
        Collection<Lesson> lessons = courseDAO.getLessonsByCourse(id);

        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if (user.getRole() == Student) {
                model.addAttribute("subStudent", !students.contains(user));
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("teachers", teachers);
        model.addAttribute("students", students);
        model.addAttribute("lessons", lessons);
        manageRools(id, session, model);

        return "coursesID";
    }

    @GetMapping("/courses/{id}/update")
    public String updateCourse(@PathVariable Long id,
                               RedirectAttributes redirectAttributes,
                               HttpSession session,
                               Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }
        model.addAttribute("courseId", id);
        return "updateCourse";
    }

    @PostMapping("/courses/{id}/update")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) String description,
                               RedirectAttributes redirectAttributes)
    {
        Course updatedCourse = courseDAO.getById(id);
        if (updatedCourse == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }
        if (name != null) {
            updatedCourse.setName(name);
        }
        if (description != null) {
            updatedCourse.setDescription(description);
        }

        try {
            courseDAO.update(updatedCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }

    @GetMapping("/courses/{id}/addLesson")
    public String addLesson(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }
        model.addAttribute("courseId", id);
        return "addLesson";
    }

    @GetMapping("/courses/{id}/addTeacher")
    public String addTeacher(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }
        Collection<User> all_teachers = userDAO.getUsersByRole(Teacher);
        Collection<User> courseTeachers = courseDAO.getTeachersByCourse(id);

        Set<Long> courseTeacherIds = courseTeachers.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Collection<User> new_teachers = all_teachers.stream()
                .filter(teacher -> !courseTeacherIds.contains(teacher.getId()))
                .toList();

        model.addAttribute("courseId", id);
        model.addAttribute("teachers", new_teachers);
        return "addTeacher";
    }

    @GetMapping("/courses/{id}/addStudent")
    public String addStudent(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }

        Collection<User> all_students = userDAO.getUsersByRole(Student);
        Collection<User> courseStudents = courseDAO.getStudentsByCourse(id);

        Set<Long> courseStudentIds = courseStudents.stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        Collection<User> new_students = all_students.stream()
                .filter(student -> !courseStudentIds.contains(student.getId()))
                .toList();

        model.addAttribute("courseId", id);
        model.addAttribute("students", new_students);
        return "addStudent";
    }

    @PostMapping("/courses/{id}/addLesson")
    public String addLesson(@PathVariable Long id,
                            @RequestParam String lessonName,
                            @RequestParam LocalDateTime lessonBegin,
                            @RequestParam LocalDateTime lessonEnd,
                            RedirectAttributes redirectAttributes)
    {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        try {
            Lesson lesson = new Lesson(lessonName, course, lessonBegin, lessonEnd);
            lessonDAO.save(lesson);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/addTeacher")
    public String addTeacher(@PathVariable Long id,
                            @RequestParam String teacherLogin,
                            RedirectAttributes redirectAttributes) {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        User user = userDAO.getByLogin(teacherLogin);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/error";
        }

        if (!user.getRole().equals(Teacher)) {
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь не является преподавателем.");
            return "redirect:/error";
        }

        try {
            TeachersCourses teacherCourse = new TeachersCourses(user, course);
            teachersCoursesDAO.save(teacherCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/addStudent")
    public String addStudent(@PathVariable Long id,
                             @RequestParam String studentLogin,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        User user = userDAO.getByLogin(studentLogin);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/error";
        }

        if (session.getAttribute("user") == null ||
                ((User) session.getAttribute("user")).getRole().equals(Student))
        {
            if (!((User) session.getAttribute("user")).equals(user)) {
                redirectAttributes.addFlashAttribute("error",
                    "Недопустимое действие");
                return "redirect:/courses/" + id;
            }
        }

        try {
            StudentsCourses studentCourse = new StudentsCourses(user, course);
            studentsCoursesDAO.save(studentCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }

    @GetMapping("/courses/{id}/delTeacher")
    public String delTeacher(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }

        Collection<User> teachers = courseDAO.getTeachersByCourse(id);
        model.addAttribute("teachers", teachers);
        model.addAttribute("courseId", id);
        return "delTeacher";
    }

    @GetMapping("/courses/{id}/delStudent")
    public String delStudent(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }

        Collection<User> students = courseDAO.getStudentsByCourse(id);
        model.addAttribute("students", students);
        model.addAttribute("courseId", id);
        return "delStudent";
    }

    @GetMapping("/courses/{id}/delLesson")
    public String delLesson(@PathVariable Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,
                             Model model)
    {
        if (!manageRools(id, session, model)) {
            redirectAttributes.addFlashAttribute("error",
                    "Изменять информацию о курсе могут только преподаватели курса!");
            return "redirect:/courses/" + id;
        }
        Collection<Lesson> lessons = courseDAO.getLessonsByCourse(id);
        model.addAttribute("lessons", lessons);
        model.addAttribute("courseId", id);
        return "delLesson";
    }

    @PostMapping("/courses/{id}/delLesson")
    public String delLesson(@PathVariable Long id,
                            @RequestParam Long lessonId,
                            RedirectAttributes redirectAttributes)
    {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }
        Lesson lesson = lessonDAO.getById(lessonId);
        if (lesson == null) {
            redirectAttributes.addFlashAttribute("error", "Занятие не найдено.");
            return "redirect:/error";
        }

        try {
            lessonDAO.delete(lesson);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/delTeacher")
    public String delTeacher(@PathVariable Long id,
                             @RequestParam String teacherLogin,
                             RedirectAttributes redirectAttributes)
    {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        User user = userDAO.getByLogin(teacherLogin);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/error";
        }

        if (!user.getRole().equals(Teacher)) {
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь не является преподавателем.");
            return "redirect:/error";
        }

        try {
            TeachersCourses teacherCourse = teachersCoursesDAO.getById(user.getId(), id);
            teachersCoursesDAO.delete(teacherCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/{id}/delStudent")
    public String delStudent(@PathVariable Long id,
                             @RequestParam String studentLogin,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        Course course = courseDAO.getById(id);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        User user = userDAO.getByLogin(studentLogin);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/error";
        }

        if (!user.getRole().equals(Student)) {
            if (session.getAttribute("user") == null || !session.getAttribute("user").equals(user)) {
                redirectAttributes.addFlashAttribute("error",
                        "Недопустимое действие");
                return "redirect:/error";
            }
        }

        try {
            StudentsCourses studentCourse = studentsCoursesDAO.getById(user.getId(), id);
            studentsCoursesDAO.delete(studentCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
    }
}