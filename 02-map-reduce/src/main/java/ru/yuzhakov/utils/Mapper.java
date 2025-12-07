package ru.yuzhakov.utils;

import java.util.List;

public interface Mapper {
    List<KeyValue> map(String fileName, String content);
}
