package ru.webprac.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import ru.webprac.classes.User;

@Controller
public class MainController {
    @ModelAttribute
    public void addAuthStatus(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        boolean isAuthenticated = user != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    @GetMapping("/")
    public String index()
    {
        return "index";
    }
}
