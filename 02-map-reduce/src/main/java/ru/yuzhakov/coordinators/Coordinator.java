package ru.yuzhakov.coordinators;

import  ru.yuzhakov.tasks.Task;
import ru.yuzhakov.utils.Mapper;
import ru.yuzhakov.utils.Reducer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Coordinator {
    private final List<String> inputFiles;
    private static final String FINAL_OUTPUT_FILE = "mr-out.txt";
    private final int numReduceTasks;
    private final Mapper mapper;
    private final Reducer reducer;
    private final Queue<Task> mapTasks = new LinkedList<>();
    private final Queue<Task> reduceTasks = new LinkedList<>();
    private final Set<Integer> completedMapTasks = new HashSet<>();
    private final Set<Integer> completedReduceTasks = new HashSet<>();
    private boolean allMapTasksDone = false;
    private boolean allReduceTasksDone = false;

    public Coordinator(List<String> inputFiles, int numReduceTasks, Mapper mapper, Reducer reducer) {
        this.inputFiles = inputFiles;
        this.numReduceTasks = numReduceTasks;
        this.mapper = mapper;
        this.reducer = reducer;

        // Создаем map задачи
        for (int i = 0; i < inputFiles.size(); i++) {
            mapTasks.add(new Task(Task.Type.MAP, i, inputFiles.get(i), null));
        }

        // Создаем reduce задачи
        for (int i = 0; i < numReduceTasks; i++) {
            reduceTasks.add(new Task(Task.Type.REDUCE, i, null, null));
        }
    }

    public synchronized Task getTask() {
        if (!mapTasks.isEmpty()) {
            return mapTasks.poll();
        } else if (!allMapTasksDone) {
            return new Task(Task.Type.DONE, -1, null, null);
        } else if (!reduceTasks.isEmpty()) {
            Task reduceTask = reduceTasks.poll();

            // Собираем все промежуточные файлы для этой reduce задачи
            List<String> files = new ArrayList<>();
            for (int mapId : completedMapTasks) {
                files.add("mr-" + mapId + "-" + reduceTask.taskId);
            }
            reduceTask.files = files;

            return reduceTask;
        } else if (!allReduceTasksDone) {
            return new Task(Task.Type.DONE, -1, null, null);
        } else {
            return new Task(Task.Type.DONE, -1, null, null);
        }
    }

    public synchronized void completeTask(Task task) {
        if (task.type == Task.Type.MAP) {
            completedMapTasks.add(task.taskId);
            if (completedMapTasks.size() == inputFiles.size()) {
                allMapTasksDone = true;
            }
        } else if (task.type == Task.Type.REDUCE) {
            completedReduceTasks.add(task.taskId);
            if (completedReduceTasks.size() == numReduceTasks) {
                allReduceTasksDone = true;
            }
        }
    }

    public synchronized void mergeResults(int numReduceTasks) throws IOException {
        // Используем TreeMap для автоматической сортировки по ключу
        Map<String, String> finalResults = new TreeMap<>();

        // Собираем результаты из всех reduce-файлов
        for (int i = 0; i < numReduceTasks; i++) {
            String filename = "mr-out-" + i;
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\t", 2);
                    if (parts.length == 2) {
                        finalResults.put(parts[0], parts[1]);
                    }
                }
            }
        }

        // Записываем объединенный результат
        try (PrintWriter out = new PrintWriter(FINAL_OUTPUT_FILE)) {
            for (Map.Entry<String, String> entry : finalResults.entrySet()) {
                out.println(entry.getKey() + "\t" + entry.getValue());
            }
        }

        // Удаляем временные reduce-файлы (опционально)
        for (int i = 0; i < numReduceTasks; i++) {
            Files.deleteIfExists(Paths.get("mr-out-" + i));
        }
    }
}
