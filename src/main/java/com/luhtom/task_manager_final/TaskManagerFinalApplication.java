/* Task Manager Webapp by Luhtom (C) - a cool license */

package com.luhtom.task_manager_final;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.TaskRepository;
import com.luhtom.task_manager_final.repository.UserRepository;

@SpringBootApplication
public class TaskManagerFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerFinalApplication.class, args);
    }

    @Bean
    CommandLineRunner demoDataLoader(UserRepository userRepository, TaskRepository taskRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            User demoUser = new User();
            demoUser.setUsername("demo");
            demoUser.setPassword(passwordEncoder.encode("demopassword"));
            demoUser.setRole("User");
            demoUser.setLastLoginDate(LocalDateTime.now().minusDays(1));
            userRepository.save(demoUser);
        };
    }
}