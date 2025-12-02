package ru.yuzhakov;

public class Main {
    public static void main(String[] args) {
        RingBuffer<String> buffer = new RingBuffer<>(5);

        // Поток-производитель
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    buffer.add("Message " + i);
                    System.out.println("Добавлено: Message " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Поток-потребитель
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String message = buffer.get();
                    System.out.println("Получено: " + message);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}