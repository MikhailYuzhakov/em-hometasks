package ru.yuzhakov.utils;

import java.util.List;

public class WordCountReducer implements Reducer {
    @Override
    public String reduce(String key, List<String> values) {
        return String.valueOf(values.size());
    }
}
