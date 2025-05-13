package ru.webprac.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.*;
import ru.webprac.classes.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

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

    @GetMapping("/courses/{id}")
    public String course(@PathVariable Long id,
                         RedirectAttributes redirectAttributes,
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

        model.addAttribute("course", course);
        model.addAttribute("teachers", teachers);
        model.addAttribute("students", students);
        model.addAttribute("lessons", lessons);

        return "redirect:/courses/" + id;
    }

    @PostMapping("/courses/add")
    public String addCourse(@RequestParam Course course,
                            RedirectAttributes redirectAttributes)
    {
        try {
            courseDAO.save(course);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + course.getId();
    }

    @PostMapping("/courses/{id}/update")
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               RedirectAttributes redirectAttributes)
    {
        Course updatedCourse = courseDAO.getById(id);
        if (updatedCourse == null) {
            redirectAttributes.addFlashAttribute("error", "Курс не найден.");
            return "redirect:/error";
        }

        updatedCourse.setName(name);
        updatedCourse.setDescription(description);
        try {
            courseDAO.update(updatedCourse);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }

        return "redirect:/courses/" + id;
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

    @PostMapping("courses/{id}/addTeacher")
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

    @PostMapping("courses/{id}/addStudent")
    public String addStudent(@PathVariable Long id,
                             @RequestParam String studentLogin,
                             RedirectAttributes redirectAttributes) {
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
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь не является обучающимся.");
            return "redirect:/error";
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

    @PostMapping("/courses/{id}/delTeacher")
    public String delTeacher(@PathVariable Long id,
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
                             RedirectAttributes redirectAttributes) {
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
            redirectAttributes.addFlashAttribute("error",
                    "Пользователь не является обучающимся.");
            return "redirect:/error";
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