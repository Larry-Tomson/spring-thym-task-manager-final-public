/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.service;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Getting and setting User object after {@link com.luhtom.task_manager_final.config.SecurityConfig}
 * filter
 *
 * <p>
 * Encoding password with BCrypt
 */
@Service
@Slf4j
public class UserService implements ApplicationListener<AuthenticationSuccessEvent> {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        log.info("SERVICE::findByUsername: {}", username);
        return userRepository.findByUsername(username);
    }

    @Override
    public void onApplicationEvent(@NonNull AuthenticationSuccessEvent event) {
        final LocalDateTime now = LocalDateTime.now();
        final String username = ((UserDetails) event //
                        .getAuthentication() //
                        .getPrincipal()) //
                        .getUsername();
        final Optional<User> optionalUser = findByUsername(username);

        if (optionalUser.isEmpty()) {
            log.error("SERVICE::findByUserName:Username not found for {} ", username);
        }

        final User user = optionalUser.get();

        log.info("SERVICE::onAuthEvent:setting user: {} new last login date: {}", user, now);
        user.setLastLoginDate(now);
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
