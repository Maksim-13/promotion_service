package faang.school.promotionservice.service.request;


import faang.school.promotionservice.dto.PromotionExpiredDto;
import faang.school.promotionservice.dto.PromotionRequestDto;
import faang.school.promotionservice.dto.PromotionResponseDto;
import faang.school.promotionservice.entity.db.Promotion;

import java.util.List;

public interface PromotionService {

    PromotionResponseDto createPromotion(PromotionRequestDto requestDto);

    void expiredPromotion(List<PromotionExpiredDto> requestDto);

    void reindexAllPromo();

    void promoUpdate(List<Long> promoIds);

    List<Promotion> getUsersToNotify(int remainingViewsThreshold, long secondsBeforeExpiry);

    void stockAlertPublish(List<Promotion> promotions);

    List<Long> balancesSyncs();
}

