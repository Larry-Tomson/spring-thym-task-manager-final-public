/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.controller;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.service.UserService;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple landing page "/" -> home.html (on auth)
 *
 * <p>
 * Initially home would be dashboard but I can't share the user repository just to show one line of
 * html
 */
@Controller
@RequestMapping("/home")
@Log4j2
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String home(Model model) {
        log.info("GET::HOME:rendering page");
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isEmpty()) {
            log.error("GET::HOME:unable to get user from username: {}", username);
            throw new UsernameNotFoundException(username);
        }

        final User user = optionalUser.get();
        final String role = user.getRole();
        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        final String fmtDate = user.getLastLoginDate().format(fmt);

        model.addAttribute("role", role);
        model.addAttribute("username", username);
        model.addAttribute("lastLoginDate", fmtDate);

        log.info("GET::HOME:Logging in as {}", user.getUsername());
        return "home";
    }
}
