/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.repository;

import com.luhtom.task_manager_final.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
