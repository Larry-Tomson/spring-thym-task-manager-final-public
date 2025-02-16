/* Task Manager Webapp by Luhtom (C) - a cool license */

package com.luhtom.task_manager_final;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskManagerFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerFinalApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            final User user = new User();
            user.setUsername("a");
            user.setPassword(passwordEncoder.encode("a"));
            user.setRole("User");
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        };
    }
}
