package ru.effective_mobile.short_url.utils;

import java.util.UUID;

public class Base62Util {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.reverse().toString();
    }

    public static String generateUniqueBase62(int length) {
        UUID uuid = UUID.randomUUID();
        long lsb = uuid.getLeastSignificantBits();
        long msb = uuid.getMostSignificantBits();

        // Комбинируем обе части UUID для получения более уникального длинного значения
        long combined = (msb << 32) | (lsb & 0xFFFFFFFFL); // Используем только 32 бита из lsb, чтобы избежать переполнения

        String base62 = encode(Math.abs(combined));

        // Обрезаем до нужной длины, если Base62 строка слишком длинная
        if (base62.length() > length) {
            return base62.substring(0, length);
        } else if (base62.length() < length) {
            // Если слишком короткая, дополняем случайными символами Base62
            StringBuilder sb = new StringBuilder(base62);
            for (int i = base62.length(); i < length; i++) {
                sb.append(ALPHABET.charAt((int) (Math.random() * BASE)));
            }
            return sb.toString();
        } else {
            return base62;
        }
    }
}
