package org.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

public class RedisMap implements Map<String, String> {
    private final JedisPool jedisPool;
    private final String mapName;

    public RedisMap(JedisPool jedisPool, String mapName) {
        this.jedisPool = jedisPool;
        this.mapName = mapName;
    }

    /**
     * Создает новый объект RedisMap со стандартным подключением (localhost:6379)
     */
    public RedisMap(String mapName) {
        this(mapName, "localhost", 6379);
    }

    /**
     * Создает новый объект RedisMap с заданными параметрами подключения.
     * @param mapName имя для этой map в Redis
     * @param host Redis хост
     * @param port Redis порт
     */
    public RedisMap(String mapName, String host, int port) {
        this.mapName = Objects.requireNonNull(mapName, "Map name cannot be null");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        this.jedisPool = new JedisPool(poolConfig, host, port);
    }

    /**
     * Создает новый объект RedisMap с существующим JedisPool
     * @param mapName имя для этой map в Redis
     * @param jedisPool существующий JedisPool
     */
    public RedisMap(String mapName, JedisPool jedisPool) {
        this.mapName = Objects.requireNonNull(mapName, "Map name cannot be null");
        this.jedisPool = Objects.requireNonNull(jedisPool, "JedisPool cannot be null");
    }



    @Override
    public int size() {
        try (Jedis jedis = jedisPool.getResource()) {
            return (int)jedis.hlen(mapName);
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hexists(mapName, key.toString());
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(mapName).contains(value.toString());
        }
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(mapName, key.toString());
        }
    }

    @Override
    public String put(String key, String value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and value cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String previousValue = jedis.hget(mapName, key);
            jedis.hset(mapName, key, value);
            return previousValue;
        }
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            String previousValue = jedis.hget(mapName, key.toString());
            jedis.hdel(mapName, key.toString());
            return previousValue;
        }
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        if (m == null) {
            throw new NullPointerException("Map cannot be null");
        }
        try (Jedis jedis = jedisPool.getResource()) {
            for (Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
                jedis.hset(mapName, entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(mapName);
        }
    }

    @Override
    public Set<String> keySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hkeys(mapName);
        }
    }

    @Override
    public Collection<String> values() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hvals(mapName);
        }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> allEntries = jedis.hgetAll(mapName);
            return allEntries.entrySet();
        }
    }

    /**
     * Закрывает JedisPool. Вызывается в момент, когда Map больше не нужен.
     */
    public void close() {
        jedisPool.close();
    }

    // Additional Redis-specific methods that might be useful

    /**
     * Устанавливает время жизни.
     * @param seconds время жизни в секундах.
     * @return true если время истекло.
     */
    public boolean expire(long seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(mapName, seconds) == 1;
        }
    }

    /**
     * Устанавливает время жизни.
     * @param unixTime timestamp когда истечет время жизни.
     * @return true если время истекло.
     */
    public boolean expireAt(long unixTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expireAt(mapName, unixTime) == 1;
        }
    }

    /**
     * Возвращает оставшееся время жизни.
     * @return время в секундах или -1, если TTL не задан.
     */
    public long ttl() {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.ttl(mapName);
        }
    }
}