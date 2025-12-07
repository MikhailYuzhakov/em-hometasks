package org.redis;

public class Main {
    public static void main(String[] args) {
        RedisMap myMap = new RedisMap("myApplicationCache");

        myMap.put("user1", "John Doe");
        myMap.put("user2", "Jane Smith");

        System.out.println("Size: " + myMap.size()); // 2
        System.out.println("User1: " + myMap.get("user1")); // John Doe

        myMap.expire(3600);

        myMap.close();
    }
}