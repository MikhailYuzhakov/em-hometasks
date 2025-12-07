package ru.yuzhakov.utils;

import java.util.List;

public interface Reducer {
    String reduce(String key, List<String> values);
}
