package ru.yuzhakov;

public class RingBuffer<T> {
    private final Object[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;
    private final Object lock = new Object();

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        buffer = new Object[capacity];
    }

    // Добавление элемента в буфер
    public void add(T element) throws InterruptedException {
        synchronized (lock) {
            while (count == buffer.length) {
                lock.wait();
            }
            buffer[tail] = element;
            tail = (tail + 1) % buffer.length;
            count++;
            lock.notifyAll();
        }
    }

    // Получение элемента из буфера
    @SuppressWarnings("unchecked")
    public T get() throws InterruptedException {
        synchronized (lock) {
            while (count == 0) {
                lock.wait();
            }
            T element = (T) buffer[head];
            buffer[head] = null; // Очистка ссылки
            head = (head + 1) % buffer.length;
            count--;
            lock.notifyAll();
            return element;
        }
    }

    // Проверка заполненности буфера
    public boolean isFull() {
        synchronized (lock) {
            return count == buffer.length;
        }
    }

    // Проверка пустоты буфера
    public boolean isEmpty() {
        synchronized (lock) {
            return count == 0;
        }
    }

    // Размер буфера
    public int size() {
        synchronized (lock) {
            return count;
        }
    }

    // Вместимость буфера
    public int capacity() {
        return buffer.length;
    }
}
