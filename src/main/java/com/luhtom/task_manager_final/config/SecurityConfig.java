/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.config;

import com.luhtom.task_manager_final.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Simple spring security authentication configuration CONFIGURATION
 *
 * <p>
 * public API endpoints := {"/register", "/h2-console/**", "/css/**"}, other endpoints are required
 * to be authenticated
 *
 * <p>
 * login url : "/login" -> "/"
 *
 * <p>
 * logout url : "/logout" -> "/login" invalidateHttpSession = true clearAuthentication = true
 */
@Configuration
public class SecurityConfig {
    private final UserDetailServiceImpl userDetailsService;

    public SecurityConfig(UserDetailServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /*
     * Google Java Style doesn't support lambda formatting well enough, local variables in lambda for
     * every trailing methods is added.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/h2-console/**").permitAll();
            auth.requestMatchers("/favicon.svg").permitAll();
            auth.requestMatchers("/register").permitAll();
            auth.requestMatchers("/css/*").permitAll();
            auth.anyRequest().authenticated();
        });
        http.formLogin(form -> {
            form.loginPage("/login");
            form.failureUrl("/login?error=true");
            form.defaultSuccessUrl("/home", true);
            form.permitAll();
        });
        http.logout(logout -> {
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/login");
            logout.invalidateHttpSession(true);
            logout.clearAuthentication(true);
            logout.permitAll();
        });
        http.authenticationProvider(authenticationProvider());
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Password encoder configuration implementing {@link BCryptPasswordEncoder }
     *
     * <p>
     * Bean being reuse in {@link com.luhtom.task_manager_final.service.UserService}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * using {@link UserDetailServiceImpl} implement {@link UserDetailsService} for retrieving a
     * username, a password, and other attributes for authenticating with a username and password.
     *
     * <p>
     * {@link PasswordEncoder} utilizing {@link BCryptPasswordEncoder} for password encryption
     *
     * @return configured {@link DaoAuthenticationProvider}
     */
    DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
