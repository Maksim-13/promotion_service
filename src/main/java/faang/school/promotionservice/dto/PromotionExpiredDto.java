package faang.school.promotionservice.dto;

import lombok.Builder;

@Builder
public record PromotionExpiredDto (

    Long id,

    Long userId,

    Long viewsUsed

) implements Dto
{}
