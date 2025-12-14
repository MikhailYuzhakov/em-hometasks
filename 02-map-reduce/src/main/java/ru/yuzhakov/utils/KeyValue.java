package ru.yuzhakov.utils;

import java.io.Serial;
import java.io.Serializable;

public class KeyValue implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;  // Добавляем serialVersionUID
    public String key;
    public String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
