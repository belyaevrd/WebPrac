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
import jakarta.validation.Valid;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.webprac.DAO.UserDAO;
import ru.webprac.classes.User;

@Controller
public class AuthController {
    @Autowired
    private UserDAO userDAO;

    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String passwd,
                        RedirectAttributes redirectAttributes,
                        HttpSession session)
    {
        User user = userDAO.getByLogin(login);
        if(user == null || !user.getPassword().equals(passwd)) {
            redirectAttributes.addFlashAttribute("error", "Неверный логин или пароль.");
            return "redirect:/error";
        }
        session.setAttribute("currentUser", user);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }

    @GetMapping("/registration")
    public String showRegistration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam User user,
                               RedirectAttributes redirectAttributes)
    {
        if(userDAO.getByLogin(user.getLogin()) != null) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при добавлении нового пользователя.");
            return "redirect:/error";
        }
        try {
            userDAO.save(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при добавлении нового пользователя.");
            return "redirect:/error";
        }
        return "login";
    }
}