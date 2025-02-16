package com.luhtom.task_manager_final.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luhtom.task_manager_final.model.Task;
import com.luhtom.task_manager_final.model.TaskStatus;
import com.luhtom.task_manager_final.service.TaskService;
import com.luhtom.task_manager_final.service.UserService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private static final String USERNAME = "testuser";

    @InjectMocks
    private TaskController controller;

    @Mock
    private Model model;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setupSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(USERNAME);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testCreateTask_withBindingErrors() {
        Task task = new Task();
        BindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        bindingResult.addError(new ObjectError("task", "Validation error"));

        String view = controller.createTask(task, bindingResult, null);

        assertEquals("tasks/list", view);
        verify(taskService, never()).save(any(Task.class));
    }

    @Test
    public void testDeleteTask() {
        Long taskId = 1L;

        String view = controller.deleteTask(taskId);

        assertEquals("redirect:/tasks", view);
        verify(taskService, times(1)).deleteById(taskId);
    }

    @Test
    public void testShowCreateForm() {
        String view = controller.showCreateForm(model);

        assertEquals("tasks/create", view);
        verify(model, times(1)).addAttribute(eq("task"), any(Task.class));
    }

    @Test
    public void testShowEditForm_taskFound() {
        Long taskId = 1L;
        Task task = new Task();
        Optional<Task> optionalTask = Optional.of(task);
        when(taskService.findById(taskId)).thenReturn(optionalTask);

        String view = controller.showEditForm(taskId, model);

        assertEquals("tasks/edit", view);
        verify(model).addAttribute("task", task);
        verify(model).addAttribute(eq("statuses"), eq(Arrays.asList(TaskStatus.values())));
    }

    @Test
    public void testShowEditForm_taskNotFound() {
        Long taskId = 1L;
        when(taskService.findById(taskId)).thenReturn(Optional.empty());

        String view = controller.showEditForm(taskId, model);

        assertEquals("redirect:/tasks", view);
        verify(model, never()).addAttribute(eq("task"), any());
    }
}
