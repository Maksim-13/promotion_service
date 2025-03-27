package faang.school.promotionservice.initialization;

import faang.school.promotionservice.service.cache.CacheUpdateService;
import faang.school.promotionservice.service.request.PromotionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmupService {

    private final PromotionService promotionService;
    private final CacheUpdateService cacheUpdateService;

    @PostConstruct
    public void warmup() {
        log.info("Starting cache warmup");
        promotionService.reindexAllPromo();
        cacheUpdateService.cacheUpdate();
        log.info("Cache warmup completed");
    }
}