/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.service;

import com.luhtom.task_manager_final.model.Task;
import com.luhtom.task_manager_final.model.TaskStatus;
import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    /* page */
    public Page<Task> findByUser(User user, Pageable pageable) {
        return taskRepository.findByUserPageable(user, pageable);
    }

    public Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable pageable) {
        return taskRepository.findByUserAndStatus(user, status, pageable);
    }

}
