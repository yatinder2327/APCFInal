package com.yourcompany.bankmanagement.presentation;

import com.yourcompany.bankmanagement.data.User;
import com.yourcompany.bankmanagement.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.security.Principal;

@Controller
public class PageController {

    @Autowired
    private UserServiceInterface userService;

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register"; // templates/register.html
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal) {
        if (principal != null) {
            User u = userService.findByUsername(principal.getName());
            if (u != null) {
                String r = u.getRole();
                if ("BANK_PERSON".equalsIgnoreCase(r)) return "bank-dashboard"; // templates/bank-dashboard.html
                if ("ADMIN".equalsIgnoreCase(r)) return "admin-dashboard"; // templates/admin-dashboard.html
            }
        }
        return "dashboard"; // templates/dashboard.html
    }
}


