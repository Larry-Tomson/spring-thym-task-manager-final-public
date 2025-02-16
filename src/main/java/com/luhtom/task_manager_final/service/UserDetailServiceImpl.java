/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.service;

import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        final Optional<User> oUser = userRepository.findByUsername(username);
        final User user = oUser.orElseThrow(() -> new UsernameNotFoundException( //
                        "No user found with username : " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), //
                        user.getPassword(), //
                        Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}
