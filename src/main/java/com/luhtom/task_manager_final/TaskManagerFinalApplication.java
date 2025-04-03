/* Task Manager Webapp by Luhtom (C) - a cool license */

package com.luhtom.task_manager_final;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskManagerFinalApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerFinalApplication.class, args);
    }

    @Bean
    @Profile("dev")
    // add to application.properties spring.profiles.active=development
    CommandLineRunner runner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            final User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRole("User");
            adminUser.setLastLoginDate(LocalDateTime.now());
            userRepository.save(adminUser);
        };
    }
}
