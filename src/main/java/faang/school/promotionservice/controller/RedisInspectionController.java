package faang.school.promotionservice.controller;

import faang.school.promotionservice.dto.CachePromoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisInspectionController {

    private final RedisTemplate<String, CachePromoDto> redisTemplate;

    @GetMapping("/keys")
    public ResponseEntity<List<String>> getAllRedisKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return ResponseEntity.ok(new ArrayList<>(keys));
    }

    @GetMapping("/value/{key}")
    public ResponseEntity<CachePromoDto> getRedisValue(@PathVariable String key) {
        CachePromoDto value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    @GetMapping("/entries")
    public ResponseEntity<Map<String, CachePromoDto>> getAllEntries() {
        Set<String> keys = redisTemplate.keys("*");
        Map<String, CachePromoDto> entries = new HashMap<>();

        for (String key : keys) {
            entries.put(key, redisTemplate.opsForValue().get(key));
        }

        return ResponseEntity.ok(entries);
    }

    @GetMapping("/keys-with-ttl")
    public ResponseEntity<Map<String, Long>> getAllKeysWithTtl() {
        Set<String> keys = redisTemplate.keys("*");
        Map<String, Long> result = new HashMap<>();

        Objects.requireNonNull(keys).forEach(key ->
                result.put(key, redisTemplate.getExpire(key, TimeUnit.SECONDS))
        );

        return ResponseEntity.ok(result);
    }
}
