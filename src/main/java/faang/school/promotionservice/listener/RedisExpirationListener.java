package faang.school.promotionservice.listener;

import faang.school.promotionservice.dto.PromotionExpiredDto;
import faang.school.promotionservice.service.request.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {

    public static final String PREFIX_PROMO = "promo:";
    private final PromotionService promotionService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("Key expired in Redis: {}", expiredKey);

        try {
            if (expiredKey.startsWith(PREFIX_PROMO)) {
                String promoIdStr = expiredKey.substring(PREFIX_PROMO.length());
                Long promoId = Long.parseLong(promoIdStr);

                List<PromotionExpiredDto> list = Collections.singletonList(
                        PromotionExpiredDto.builder().id(promoId).viewsUsed(0L).build()
                );
                promotionService.expiredPromotion(list);
                log.info("Sent expiration event for promoId: {}", promoId);
            } else {
                log.warn("Unexpected key format, skipping: {}", expiredKey);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid key format: {}", expiredKey, e);
        }
    }
}
