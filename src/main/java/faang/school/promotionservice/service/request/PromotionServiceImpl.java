package faang.school.promotionservice.service.request;

import faang.school.promotionservice.client.UserServiceClient;
import faang.school.promotionservice.dto.CachePromoDto;
import faang.school.promotionservice.dto.Dto;
import faang.school.promotionservice.dto.PromotionExpiredDto;
import faang.school.promotionservice.dto.PromotionRequestDto;
import faang.school.promotionservice.dto.PromotionResponseDto;
import faang.school.promotionservice.dto.StockAlertDto;
import faang.school.promotionservice.dto.UserDto;
import faang.school.promotionservice.entity.cache.SearchPromo;
import faang.school.promotionservice.entity.db.Promotion;
import faang.school.promotionservice.enums.PromotionStatus;
import faang.school.promotionservice.exception.DataValidationException;
import faang.school.promotionservice.mapper.PromoMapper;
import faang.school.promotionservice.publisher.StockAlertEventPublisher;
import faang.school.promotionservice.repository.db.PromotionRepository;
import faang.school.promotionservice.repository.nosql.ElasticPromoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    public static final String PREFIX_PROMO = "promo:";
    public static final String SEARCH_TEMPLATE = "promo:*";

    private final ElasticsearchOperations elasticsearchOperations;
    private final PromotionRepository promotionRepository;
    private final ElasticPromoRepository elasticPromoRepository;
    private final PromoMapper promoMapper;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, CachePromoDto> redisTemplate;
    private final StockAlertEventPublisher stockAlertEventPublisher;

    @Value("${elasticsearch.indices.cache-promo}")
    private String cashPromoIndex;

    @Override
    public PromotionResponseDto createPromotion(PromotionRequestDto requestDto) {

        Promotion promotion = promoMapper.toEntity(requestDto);
        promotion = promotionRepository.create(promotion.getUserId(), promotion.getTotalViews(),
                promotion.getStartDate(), promotion.getEndDate());

        //payment request stub

        return promoMapper.toDto(promotion);
    }

    @Override
    @Transactional
    public void expiredPromotion(List<PromotionExpiredDto> expiredPromotions) {
        expiredPromotions.forEach(promoDto -> {
                    validateRequestDto(promoDto);
                    promotionRepository.updateStatusAndTotalViews(promoDto.id(),
                            PromotionStatus.EXPIRED, promoDto.viewsUsed());
                    log.info("expired promotion: {}", promoDto.id());
                }
        );
    }

    // service activation
    public void promoActivate(PromotionRequestDto requestDto) {
        validateRequestDto(requestDto);
        Promotion promotion = promoMapper.updateStatus(requestDto, PromotionStatus.ACTIVE);
        updatePromotion(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public void reindexAllPromo() {
        if (elasticsearchOperations.indexOps(IndexCoordinates.of(cashPromoIndex)).exists()) {
            elasticsearchOperations.indexOps(IndexCoordinates.of(cashPromoIndex)).refresh();
            log.info("reindexation has been completed");
        }

        List<UserDto> users = userServiceClient.getUsers();

        if(users.isEmpty()){
            log.info("No users found");
            return;
        }

        elasticPromoRepository.saveAll(
                users.stream().map(
                        user -> new SearchPromo(
                                user.id(),
                                user.title(),
                                user.description(),
                                user.aboutMe(),
                                user.country(),
                                user.city(),
                                user.id()
                        )
                ).toList()
        );
    }

    private List<UserDto> getUserDtos(List<Promotion> promotions) {
        List<Long> userIds = getUserIds(promotions);
        return userServiceClient.getUsersByIds(userIds);
    }

    private List<UserDto> getUserDtos() {
        return userServiceClient.getUsers();
    }

    private static List<Long> getUserIds(List<Promotion> promotions) {
        return promotions.stream()
                .map(Promotion::getUserId)
                .distinct().toList();
    }

    @Override
    public void promoUpdate(List<Long> promoIds) {
        List<Promotion> promotions = promotionRepository.findByIdIn(promoIds);

        for (Promotion promo : promotions) {
            CachePromoDto dto = redisTemplate.opsForValue().get(PREFIX_PROMO + promo.getId());
            if (dto != null) {
                promo.setRemainingViews(dto.getViewsUsed());
            }
        }
        promotionRepository.saveAll(promotions);
    }

    @Override
    public List<Promotion> getUsersToNotify(int remainingViewsThreshold, long secondsBeforeExpiry) {
        LocalDateTime timeThreshold = LocalDateTime.now().plusSeconds(secondsBeforeExpiry);
        return promotionRepository.findUserIdsWithLowRemainingViewsOrNearExpiry(
                remainingViewsThreshold,
                timeThreshold
        );
    }

    @Transactional
    @Override
    public void stockAlertPublish(List<Promotion> promotions) {
        List<UserDto> users = getUserDtos(promotions);

        List<StockAlertDto> stockAlerts = users.stream()
                .map(promoMapper::toStockAlertDto)
                .distinct().toList();

        List<Long> userIds = getUserIds(promotions);
        stockAlertEventPublisher.publish(stockAlerts);
        promotionRepository.markAsNotified(userIds);
    }

    @Override
    public List<Long> balancesSyncs() {
        Set<String> keys = redisTemplate.keys(SEARCH_TEMPLATE);
        if (keys == null || keys.isEmpty()) {
            return null;
        }

        List<Long> promoIds = new ArrayList<>();
        for (String key : keys) {
            CachePromoDto dto = redisTemplate.opsForValue().get(key);
            if (dto != null) {
                promoIds.add(dto.getPromoId());
            }
        }
        return promoIds;
    }

    private void updatePromotion(Promotion promotion) {
        validateRecord(promotion.getId());
        promotion = promotionRepository.save(promotion);
        promoMapper.toDto(promotion);
    }

    private void validateRecord(Long id) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(id);

        if (!promotionOptional.isPresent()) {
            throw new RuntimeException("Promotion with id " + id + " not found");
        }
    }

    private void validateRequestDto(Dto requestDto) {
        if (requestDto.id() == null) {
            throw new DataValidationException("Promotion with id " + requestDto.id()
                    + " can't update Promotion with id");
        }
    }
}
