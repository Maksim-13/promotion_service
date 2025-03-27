package faang.school.promotionservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PromotionRequestDto(
        Long id,

        @NotBlank
        Long userId,

        Long totalViews,

        Long monetaryAsset,

        LocalDateTime startDate,

        LocalDateTime endDate
) implements Dto {
}

