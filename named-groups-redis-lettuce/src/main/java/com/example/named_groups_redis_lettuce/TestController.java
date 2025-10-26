package com.example.named_groups_redis_lettuce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/test-redis")
    public String testRedis() {
        try {
            redisTemplate.opsForValue().set("test-key", "SSL connection works!");
            String value = redisTemplate.opsForValue().get("test-key");
            return "Redis SSL connection successful: " + value;
        } catch (Exception e) {
            return "Redis connection failed: " + e.getMessage();
        }
    }
}
