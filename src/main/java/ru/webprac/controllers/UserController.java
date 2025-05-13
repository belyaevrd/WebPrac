package ru.webprac.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.UserDAO;
import ru.webprac.classes.Lesson;
import ru.webprac.classes.User;

import java.util.Collection;

@Controller
public class UserController {
    @Autowired
    private UserDAO userDAO;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userDAO.getAll());
        return "users";
    }

    @GetMapping("/profile")
    public String showCurrentProfile(HttpSession session,
                                     Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
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
        return "users/" + id;
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String login,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        currentUser.setLogin(login);
        currentUser.setName(name);
        currentUser.setPassword(password);
        try {
            userDAO.update(currentUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
        return "profile";
    }

    @GetMapping("/users/{id}/timetable")
    public String showTimetable(@PathVariable Long id,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            User user = userDAO.getById(id);
            Collection<Lesson> timetable = userDAO.getTimetable(user);
            model.addAttribute("user", user);
            model.addAttribute("timetable", timetable);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
        return "redirect:/users/" + id + "/timetable";
    }
}