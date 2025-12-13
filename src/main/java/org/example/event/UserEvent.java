package org.example.event;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserEvent {
    private String email;
    private String operation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    public UserEvent() {
    }

    public UserEvent(String email, String operation) {
        this.email = email;
        this.operation = operation;
        this.timestamp = LocalDateTime.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserEvent{" +
                "email='" + email + '\'' +
                ", operation='" + operation + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}