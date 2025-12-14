package ru.yuzhakov;

import ru.yuzhakov.coordinators.Coordinator;
import ru.yuzhakov.utils.WordCountMapper;
import ru.yuzhakov.utils.WordCountReducer;
import ru.yuzhakov.workers.Worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java MapReduce <inputFiles...> <numReduceTasks> <numWorkers>");
            return;
        }

        List<String> inputFiles = new ArrayList<>(Arrays.asList(args).subList(0, args.length - 2));
        int numReduceTasks = Integer.parseInt(args[args.length - 2]);
        int numWorkers = Integer.parseInt(args[args.length - 1]);

        Coordinator coordinator = new Coordinator(
                inputFiles,
                numReduceTasks,
                new WordCountMapper(),
                new WordCountReducer());

        // Запускаем рабочих
        try (ExecutorService executor = Executors.newFixedThreadPool(numWorkers)) {
            for (int i = 0; i < numWorkers; i++) {
                executor.submit(new Worker(coordinator, new WordCountMapper(), new WordCountReducer(), numReduceTasks));
            }

            executor.shutdown();
            if (executor.awaitTermination(1, TimeUnit.HOURS)) {
                // Объединяем результаты после завершения всех задач
                coordinator.mergeResults(numReduceTasks);
            }


        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("MapReduce job completed");
    }
}