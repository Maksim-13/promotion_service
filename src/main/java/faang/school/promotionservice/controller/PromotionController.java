package faang.school.promotionservice.controller;


import faang.school.promotionservice.dto.PromotionExpiredDto;
import faang.school.promotionservice.dto.PromotionRequestDto;
import faang.school.promotionservice.dto.PromotionResponseDto;
import faang.school.promotionservice.service.request.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping()
    public PromotionResponseDto create(@RequestBody PromotionRequestDto requestDto) {
        return promotionService.createPromotion(requestDto);
    }

    @PostMapping("/expired")
    public void expired(@RequestBody List<PromotionExpiredDto> requestDto) {
        promotionService.expiredPromotion(requestDto);
    }

    @GetMapping("/reindex")
    public void reindexPromo() {
        promotionService.reindexAllPromo();
    }
}
