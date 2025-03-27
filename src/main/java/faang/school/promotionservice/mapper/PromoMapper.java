package faang.school.promotionservice.mapper;

import faang.school.promotionservice.dto.PromotionRequestDto;
import faang.school.promotionservice.dto.PromotionResponseDto;
import faang.school.promotionservice.dto.StockAlertDto;
import faang.school.promotionservice.dto.UserDto;
import faang.school.promotionservice.entity.db.Promotion;
import faang.school.promotionservice.enums.PromotionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromoMapper {

    @Mapping(target = "promotionStatus", ignore = true)
    Promotion toEntity(PromotionRequestDto dto);

    PromotionResponseDto toDto(Promotion entity);

    @Mapping(target = "id", ignore = true)
    PromotionResponseDto toEvent(PromotionRequestDto requestDto);

    Promotion updateStatus(PromotionRequestDto requestDto, PromotionStatus promotionStatus);

    @Mapping(target = "userId", source = "id")
    StockAlertDto toStockAlertDto(UserDto userDto);
}
