package faang.school.promotionservice.schedule;

import faang.school.promotionservice.entity.db.Promotion;
import faang.school.promotionservice.service.request.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockSyncManager {

    @Value("${promotion.notification.remaining-views-threshold}")
    private final int remainingViewsThreshold;

    @Value("${promotion.notification.seconds-before-expiry}")
    private final long secondsBeforeExpiry;

    private final PromotionService promotionService;

    @Scheduled(
            fixedDelayString = "${spring.scheduled.fixed-delay}",
            initialDelayString = "${spring.scheduled.initial-delay}"
    )
    public void syncsBalancesCheck() {
        List<Long> promoIds = promotionService.balancesSyncs();
        if (promoIds == null || promoIds.isEmpty()) {
            return;
        }

        promotionService.promoUpdate(promoIds);
        balancesCheck();
    }

    private void balancesCheck() {
        List<Promotion> promotions = promotionService.getUsersToNotify(remainingViewsThreshold, secondsBeforeExpiry);

        if(promotions != null && !promotions.isEmpty()){
            promotionService.stockAlertPublish(promotions);
        }
    }
}
