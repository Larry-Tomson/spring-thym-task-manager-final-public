package com.luhtom.task_manager_final.controller;

import com.luhtom.task_manager_final.model.Task;
import com.luhtom.task_manager_final.model.TaskStatus;
import com.luhtom.task_manager_final.model.User;
import com.luhtom.task_manager_final.service.TaskService;
import com.luhtom.task_manager_final.service.UserService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/tasks")
@Log4j2
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute("task") Task task, BindingResult bindingResult, Model model) {
        log.info("POST::CREATE::creating new task with id: {}", task.getId());

        if (bindingResult.hasErrors()) {
            final List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError err : allErrors) {
                log.error("POST::CREATE:: {} {} ", err.getObjectName(), err.getCode());
            }
            model.addAttribute("errors", allErrors);
            return "error";
        }

        final User currentUser = getUser();
        task.setUser(currentUser);
        log.debug("POST::CREATE::Creating task for username: {}, title: {}", currentUser.getUsername(),
                task.getTitle());
        taskService.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        log.info("POST::DELETE: Task id: {}", id);
        final Optional<Task> optionalTask = taskService.findById(id);

        if (optionalTask.isEmpty()) {
            log.error("POST::DELETE: Task not found with id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        final Task task = optionalTask.get();
        final User currentUser = getUser();

        if (!task.getUser().getId().equals(currentUser.getId())) {
            log.error("POST::DELETE: Unauthorized deletion attempt by user: {} on task id: {}",
                    currentUser.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to delete this task");
        }

        taskService.deleteById(id);
        return "redirect:/tasks";
    }

    @GetMapping("/feature")
    public String listTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        final User currentUser = getUser();
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        final int PAGE_SIZE = 10;
        final Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
        Page<Task> taskPage;

        if (status != null) {
            taskPage = taskService.findByUserAndStatus(currentUser, status, pageable);
        } else {
            taskPage = taskService.findByUser(currentUser, pageable);
        }

        log.info("GET::LIST: Rendering task list for user: {}, page: {}, sort: {}, direction: {}",
                currentUser.getUsername(), page, sortBy, direction);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("totalItems", taskPage.getTotalElements());
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reversedDirection", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("role", currentUser.getRole());

        return "tasks/list";
    }

    @GetMapping
    public String listTasks(Model model) {
        final User currentUser = getUser();
        final List<Task> tasks = taskService.findByUser(currentUser);

        log.info("GET::LIST: Rendering task list for user: {}", currentUser.getUsername());
        log.debug("GET::LIST: Showing {} task(s)", tasks.size());
        model.addAttribute("tasks", tasks);

        final User user = getUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());

        return "tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        log.info("GET::CREATE: Rendering create task form");
        model.addAttribute("task", new Task());

        final User user = getUser();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());

        return "tasks/create";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        final Optional<Task> optionalTask = taskService.findById(id);

        if (optionalTask.isEmpty()) {
            log.error("GET::EDIT: Unable to find task id: {}", id);
            return "redirect:/tasks";
        }

        final Task task = optionalTask.get();
        final User user = getUser();

        if (!task.getUser().getId().equals(user.getId())) {
            log.error("GET::EDIT: Unauthorized edit attempt by user: {} on task id: {}", user.getUsername(), id);
            throw new AccessDeniedException("You are not allowed to edit this task");
        }

        log.info("GET::EDIT: Editing task with title: {}", task.getTitle());
        model.addAttribute("task", task);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", user.getRole());
        model.addAttribute("statuses", Arrays.asList(TaskStatus.values()));
        return "tasks/edit";
    }

    @PostMapping("/{id}")
    public String updateTask(@PathVariable Long id, @Valid @ModelAttribute("task") Task task,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            final List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError err : allErrors) {
                log.error("POST::EDIT: {} {} {} ", id, err.getObjectName(), err.getCode());
            }
            model.addAttribute("errors", allErrors);
            return "tasks/error";
        }

        final Optional<Task> optionalTask = taskService.findById(id);

        if (optionalTask.isEmpty()) {
            log.error("POST::EDIT: Task not found with id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        final Task existingTask = optionalTask.get();
        final User currentUser = getUser();

        if (!existingTask.getUser().getId().equals(currentUser.getId())) {
            log.error("POST::EDIT: Unauthorized update attempt by user: {} on task id: {}", currentUser.getUsername(),
                    id);
            throw new AccessDeniedException("You are not allowed to update this task");
        }

        task.setId(id);
        task.setUser(currentUser);
        log.debug("POST::EDIT: Updating task for user: {} with id: {}", currentUser.getUsername(), id);
        taskService.save(task);
        return "redirect:/tasks";
    }

    private User getUser() {
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        final Optional<User> user = userService.findByUsername(username);
        log.info("TASK_CONTROLLER::Retrieving user for username: {}", username);

        if (user.isEmpty()) {
            log.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found for " + username);
        }

        return user.get();
    }
}
