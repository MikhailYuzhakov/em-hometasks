package com.emobile.springtodo.repositories;

import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class SpringDataTaskDaoImpl implements TaskDao {
    private final TaskRepository repository;

    @Override
    public Optional<List<Task>> findAllWithPagination(int limit, int offset) {
        int pageNumber = offset / limit;

        Page<Task> page = repository.findAll(
                PageRequest.of(pageNumber, limit, Sort.by("id"))
        );

        if (page.isEmpty()) {
            return Optional.empty();
        }

        log.info("repo task = {}", page.getContent());
        return Optional.of(page.getContent());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Task save(Task task) {
        return repository.save(task);
    }

    @Override
    public void delete(Task task) {
        repository.delete(task);
    }

    @Override
    public Task update(Long id, Task task) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        task.setId(id);

        return repository.save(task);
    }

    @Override
    public Optional<List<Task>> findAllByStatus(boolean isCompleted) {
        List<Task> tasks = repository.findByCompleted(isCompleted);

        log.info("tasks with status {} : {}", isCompleted, tasks);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks);
    }

    @Override
    public Integer countTasks() {
        return (int) repository.count();
    }
}
