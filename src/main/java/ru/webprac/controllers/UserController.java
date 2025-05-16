package ru.webprac.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.StudentsCoursesDAO;
import ru.webprac.DAO.TeachersCoursesDAO;
import ru.webprac.DAO.UserDAO;
import ru.webprac.classes.Lesson;
import ru.webprac.classes.User;
import ru.webprac.classes.UserRole;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
public class UserController {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TeachersCoursesDAO teachersCoursesDAO;

    @Autowired
    private StudentsCoursesDAO studentsCoursesDAO;

    @GetMapping("/users")
    public String listUsers(RedirectAttributes redirectAttributes,
                            HttpSession session,
                            Model model) {
        if (session.getAttribute("user") == null ||
                !((User) session.getAttribute("user")).getLogin().equals("admin")) {
            redirectAttributes.addFlashAttribute("error", "У вас нет доступа к этой странице.");
            return "redirect:/error";
        }
        model.addAttribute("users", userDAO.getAll());
        return "redirect:/users";
    }

    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", userDAO.getUsersByRole(UserRole.Teacher));
        model.addAttribute("teachersCourses", teachersCoursesDAO.getAll());
        return "teachers";
    }

    @GetMapping("/profile")
    public String showCurrentProfile(HttpSession session,
                                     Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        model.addAttribute("courses", userDAO.getCoursesByLogin(user.getLogin()));
        return "profile";
    }

    @GetMapping("/users/{id}")
    public String showProfile(@PathVariable Long id,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = userDAO.getById(id);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден.");
            return "redirect:/error";
        }
        model.addAttribute("user", user);
        model.addAttribute("courses", userDAO.getCoursesByLogin(user.getLogin()));
        return "usersID";
    }

    @GetMapping("/profile/update")
    public String updateProfile(Model model)
    {
        return "updateProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String login,
                                @RequestParam(required = false) String password,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                Model model)
    {
        if (session.getAttribute("user") == null) {
            return "login";
        }
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }
        if (name != null && !name.isEmpty()) {
            currentUser.setName(name);
        }
        if (login != null && !login.isEmpty()) {
            if (userDAO.getByLogin(login) != null) {
                redirectAttributes.addFlashAttribute("error",
                        "Данный логин уже занят");
                return "redirect:/profile/update";
            }
            if (login.matches(".*\\s.*")) {
                redirectAttributes.addFlashAttribute("error",
                        "Логин не должен содержать пробельных символов");
                return "redirect:/profile/update";
            }
            currentUser.setLogin(login);
        }
        if (password != null && !password.isEmpty()) {
            currentUser.setPassword(password);
        }
        try {
            userDAO.update(currentUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
        session.setAttribute("user", currentUser);
        session.setAttribute("userName", currentUser.getName());
        model.addAttribute("user", currentUser);
        return "redirect:/profile";
    }

    @GetMapping("/timetable")
    public String showTimetable(Model model) {
        model.addAttribute("users", userDAO.getAll());
        return "timetable";
    }

    @PostMapping("/timetable/{id}")
    public String showTimetable(@PathVariable Long id,
                                @RequestParam(required = false) LocalDateTime begin,
                                @RequestParam(required = false) LocalDateTime end,
                                Model model)
    {
        User user = userDAO.getById(id);
        Collection<Lesson> timetable = userDAO.getTimetable(user, begin, end);
        model.addAttribute("user", user);
        model.addAttribute("timetable", timetable);
        return "timetableId";
    }

    @GetMapping("/timetable/show")
    public String getTimetable(@RequestParam Long id,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime begin,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                RedirectAttributes redirectAttributes,
                                Model model)
    {
        if (begin != null && end != null && begin.isAfter(end)) {
            redirectAttributes.addFlashAttribute("error", "Неправильный временной интервал");
            return "redirect:/timetable";
        }
        User user = userDAO.getById(id);
        Collection<Lesson> timetable = userDAO.getTimetable(user, begin, end);
        model.addAttribute("user", user);
        model.addAttribute("timetable", timetable);
        model.addAttribute("begin", begin);
        model.addAttribute("end", end);
        return "showTimetable";
    }

}