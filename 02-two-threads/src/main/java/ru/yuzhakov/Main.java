package ru.yuzhakov;

public class Main {
    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                if (i % 2 == 0) System.out.println("Thread 1: " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                if (i % 2 != 0) System.out.println("Thread 2: " + i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }).start();
    }
}