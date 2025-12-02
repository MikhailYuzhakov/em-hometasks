package com.emobile.springtodo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Task(Long id, String description, String title, boolean completed) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.completed = completed;
    }
}
