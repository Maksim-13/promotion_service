package faang.school.promotionservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CachePromoDto {

    private Long promoId;

    private Long userId;

    private Integer viewsUsed;
}

