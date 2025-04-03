/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.repository;

import com.luhtom.task_manager_final.model.Task;
import com.luhtom.task_manager_final.model.TaskStatus;
import com.luhtom.task_manager_final.model.User;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable pageable);
}
