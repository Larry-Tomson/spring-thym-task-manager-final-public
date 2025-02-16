/* Task Manager Webapp by Luhtom (C) - a cool license */
package com.luhtom.task_manager_final.model;

public enum TaskStatus {
    COMPLETED("completed"), IN_PROGRESS("in progress"), PENDING("pending");

    private String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
