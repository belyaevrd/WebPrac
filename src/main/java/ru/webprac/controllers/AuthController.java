package ru.webprac.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.UserDAO;
import ru.webprac.classes.User;
import ru.webprac.classes.UserRole;

@Controller
public class AuthController {
    @Autowired
    private UserDAO userDAO;

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session)
    {
        User user = userDAO.getByLogin(login);
        if(user == null || !user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error",
                    "Неверный логин или пароль");
            return "redirect:/login";
        }
        session.setAttribute("user", user);
        session.setAttribute("userName", user.getName());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/registration")
    public String showRegistration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String login,
                               @RequestParam String name,
                               @RequestParam String role,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes,
                               HttpSession session)
    {
        if (session.getAttribute("user") != null) {
            session.invalidate();
        }
        UserRole real_role = role.equals("Teacher") ? UserRole.Teacher : UserRole.Student;
        User user = new User(login, name, real_role, password);
        if(userDAO.getByLogin(login) != null) {
            redirectAttributes.addFlashAttribute("error",
                    "Данный логин уже занят");
            return "redirect:/registration";
        }
        if (login.matches(".*\\s.*")) {
            redirectAttributes.addFlashAttribute("error",
                    "Логин не должен содержать пробельных символов");
            return "redirect:/registration";
        }
        try {
            userDAO.save(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при добавлении нового пользователя");
            return "redirect:/registration";
        }

        session.setAttribute("user", user);
        session.setAttribute("userName", user.getName());
        return "redirect:/";
    }
}