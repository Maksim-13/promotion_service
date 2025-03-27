package faang.school.promotionservice.service.cache;

import faang.school.promotionservice.dto.CachePromoDto;
import faang.school.promotionservice.entity.db.Promotion;
import faang.school.promotionservice.enums.PromotionStatus;
import faang.school.promotionservice.repository.db.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class CacheUpdateServiceImpl implements CacheUpdateService {

    public static final String PREFIX_PROMO = "promo:";
    public static final String PATTERN_PROMO = "promo:*";
    private final PromotionRepository promotionRepository;
    private final RedisTemplate<String, CachePromoDto> redisTemplate;

    @Override
    public void cacheUpdate() {

        List<Promotion> promotions = promotionRepository.findPromotionsByStatusInDateRange(PromotionStatus.ACTIVE);
        cacheClean();

        promotions.forEach(promo ->
        {
            String key = PREFIX_PROMO + promo.getId();
            long ttl = ChronoUnit.SECONDS.between(LocalDateTime.now(), promo.getEndDate());
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, new CachePromoDto(promo.getId(),
                        promo.getUserId(), promo.getRemainingViews()), ttl, TimeUnit.SECONDS);
                log.info("Active promo: promoID: {}, userId: {}, viewsUsed: {}, ttl: {}", key, promo.getUserId(),
                        promo.getRemainingViews(), ttl);
            }
        }
        );
    }

    private void cacheClean() {
        Set<String> keys = redisTemplate.keys(PATTERN_PROMO);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("Deleted old entries from Redis: {}", keys.size());
        }
    }
}
