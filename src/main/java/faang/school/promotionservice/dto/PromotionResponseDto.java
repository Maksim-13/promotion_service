package faang.school.promotionservice.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record PromotionResponseDto(

        Long id,

        @NotBlank
        Long userId,

        long totalViews,

        @NotBlank
        LocalDateTime startDate,

        @NotBlank
        LocalDateTime endDate
) {
}




