package com.emobile.springtodo.repositories;

import com.emobile.springtodo.exceptions.TaskNotFoundException;
import com.emobile.springtodo.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HibernateTaskDaoImpl implements TaskDao {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Task>> findAllWithPagination(int limit, int offset) {
        String jpql = "SELECT t FROM Task t ORDER BY t.id";
        TypedQuery<Task> query = entityManager.createQuery(jpql, Task.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Task> tasks = query.getResultList();
        log.info("repo task = {}", tasks);

        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        Task task = entityManager.find(Task.class, id);
        return Optional.ofNullable(task);
    }

    @Override
    public Task save(Task task) {
        entityManager.persist(task);
        return task;
    }

    @Override
    public void delete(Task task) {
        Task managedTask = entityManager.contains(task) ? task : entityManager.merge(task);
        entityManager.remove(managedTask);
    }

    @Override
    public Task update(Long id, Task task) {
        Task existingTask = entityManager.find(Task.class, id);

        if (existingTask == null) {
            throw new TaskNotFoundException("Task not found with id " + id);
        }

        task.setId(id);
        return entityManager.merge(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Task>> findAllByStatus(boolean isCompleted) {
        String jpql = "SELECT t FROM Task t WHERE t.completed = :isCompleted";

        List<Task> tasks = entityManager.createQuery(jpql, Task.class)
                .setParameter("isCompleted", isCompleted)
                .getResultList();

        log.info("tasks with status {} : {}", isCompleted, tasks);
        return tasks.isEmpty() ? Optional.empty() : Optional.of(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer countTasks() {
        String jpql = "SELECT COUNT(t) FROM Task t";
        Long count = entityManager.createQuery(jpql, Long.class).getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}
