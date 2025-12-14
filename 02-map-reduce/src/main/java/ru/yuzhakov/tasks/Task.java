package ru.yuzhakov.tasks;

import java.util.List;

public class Task {
    public enum Type { MAP, REDUCE, DONE }
    public Type type;
    public int taskId;
    public String fileName;
    public List<String> files;

    public Task(Type type, int taskId, String fileName, List<String> files) {
        this.type = type;
        this.taskId = taskId;
        this.fileName = fileName;
        this.files = files;
    }
}
