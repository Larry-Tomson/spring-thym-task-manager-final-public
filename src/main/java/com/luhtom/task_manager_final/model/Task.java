/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/*
 * Title maximum chars: 80 Task - Entities User 1:M Task task.status = Status.PENDING
 *
 * Task -->
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
public class Task {
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TaskStatus.PENDING;
        }
    }
}
