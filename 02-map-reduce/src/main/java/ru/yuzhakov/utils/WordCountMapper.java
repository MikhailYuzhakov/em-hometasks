package ru.yuzhakov.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WordCountMapper implements Mapper {
    @Override
    public List<KeyValue> map(String fileName, String content) {
        String[] words = content.split("\\s+");
        return Arrays.stream(words)
                .map(word -> new KeyValue(word, "1"))
                .collect(Collectors.toList());
    }
}
