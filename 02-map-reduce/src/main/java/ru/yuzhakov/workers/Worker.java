package ru.yuzhakov.workers;

import ru.yuzhakov.coordinators.Coordinator;
import ru.yuzhakov.utils.KeyValue;
import ru.yuzhakov.utils.Mapper;
import ru.yuzhakov.utils.Reducer;
import ru.yuzhakov.tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Worker implements Runnable {
    private static final String INTERMEDIATE_FILE_PREFIX = "mr-";
    private static final String OUTPUT_FILE_PREFIX = "mr-out-";
    private static final String KV_SEPARATOR = "|";

    private final Coordinator coordinator;
    private final Mapper mapper;
    private final Reducer reducer;
    private final int numReduceTasks;

    public Worker(Coordinator coordinator, Mapper mapper, Reducer reducer, int numReduceTasks) {
        this.coordinator = coordinator;
        this.mapper = mapper;
        this.reducer = reducer;
        this.numReduceTasks = numReduceTasks;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task = coordinator.getTask();

                if (task.type == Task.Type.DONE) {
                    break;
                }

                switch (task.type) {
                    case MAP:
                        processMapTask(task);
                        break;
                    case REDUCE:
                        processReduceTask(task);
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Worker error: " + e.getMessage());
        }
    }

    private void processMapTask(Task task) throws IOException {
        // 1. Чтение входного файла
        String content = readFileContent(task.fileName);

        // 2. Выполнение map-функции
        List<KeyValue> kvs = mapper.map(task.fileName, content);

        // 3. Группировка по reduce-задачам
        Map<Integer, List<KeyValue>> buckets = new HashMap<>();
        for (KeyValue kv : kvs) {
            int reduceId = getReduceTaskId(kv.key);
            buckets.computeIfAbsent(reduceId, k -> new ArrayList<>()).add(kv);
        }

        // 4. Запись в промежуточные файлы
        for (Map.Entry<Integer, List<KeyValue>> entry : buckets.entrySet()) {
            String filename = getIntermediateFilename(task.taskId, entry.getKey());
            writeKeyValuesToFile(filename, entry.getValue());
        }

        // 5. Уведомление координатора
        coordinator.completeTask(task);
    }

    private void processReduceTask(Task task) throws IOException {
        // 1. Чтение всех промежуточных файлов
        List<KeyValue> allKvs = new ArrayList<>();
        for (String file : task.files) {
            allKvs.addAll(readKeyValuesFromFile(file));
        }

        // 2. Сортировка и группировка
        allKvs.sort(Comparator.comparing(kv -> kv.key));
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        for (KeyValue kv : allKvs) {
            grouped.computeIfAbsent(kv.key, k -> new ArrayList<>()).add(kv.value);
        }

        // 3. Применение reduce и запись результата
        String outputFilename = getOutputFilename(task.taskId);
        try (PrintWriter out = new PrintWriter(outputFilename)) {
            for (Map.Entry<String, List<String>> entry : grouped.entrySet()) {
                String result = reducer.reduce(entry.getKey(), entry.getValue());
                out.println(entry.getKey() + "\t" + result);
            }
        }

        // 4. Уведомление координатора
        coordinator.completeTask(task);
    }

    // Вспомогательные методы
    private int getReduceTaskId(String key) {
        return Math.abs(key.hashCode()) % numReduceTasks;
    }

    private String getIntermediateFilename(int mapTaskId, int reduceTaskId) {
        return INTERMEDIATE_FILE_PREFIX + mapTaskId + "-" + reduceTaskId;
    }

    private String getOutputFilename(int reduceTaskId) {
        return OUTPUT_FILE_PREFIX + reduceTaskId;
    }

    private String readFileContent(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    private void writeKeyValuesToFile(String filename, List<KeyValue> kvs) throws IOException {
        try (PrintWriter out = new PrintWriter(filename)) {
            for (KeyValue kv : kvs) {
                out.println(kv.key + KV_SEPARATOR + kv.value);
            }
        }
    }

    private List<KeyValue> readKeyValuesFromFile(String filename) throws IOException {
        List<KeyValue> kvs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\" + KV_SEPARATOR);
                if (parts.length == 2) {
                    kvs.add(new KeyValue(parts[0], parts[1]));
                }
            }
        }
        return kvs;
    }
}