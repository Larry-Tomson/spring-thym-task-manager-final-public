/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.controller;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.service.UserService;
import jakarta.validation.Valid;

import static org.mockito.ArgumentMatchers.refEq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
@Log4j2
public class RegisterController {
    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * @param user User object from view
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("user") User user, //
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            final List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError err : allErrors) {
                log.error("POST::REGISTER: {}", err);
            }
            model.addAttribute("errors", allErrors);
            return "register";
        }

        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            log.warn("POST::REGISTER:username already exists:: {}", user.getUsername());
            bindingResult.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }

        log.info("POST::REGISTER:Saving user {}", user.getUsername());
        user.setLastLoginDate(LocalDateTime.now());
        userService.save(user);
        log.debug("POST::REGISTER:login successfully {}", user.getUsername());
        return "redirect:/login";
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
}
