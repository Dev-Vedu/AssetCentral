package com.itasset.management.controller;

import com.itasset.management.model.User;
import com.itasset.management.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            return "redirect:/admin/dashboard";
        }

        User user = userService.login(email, password);

        if (user == null) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        session.setAttribute("user", user);

        if ("EMPLOYEE".equals(user.getRole())) {
            return "redirect:/employee/dashboard";
        }
        else if ("WORKER".equals(user.getRole())) {
            return "redirect:/worker/dashboard";
        }
        else {
            return "redirect:/";
        }
    }


    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // destroy session
        return "redirect:/";
    }

}
