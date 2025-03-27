package faang.school.promotionservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.promotionservice.config.redis.Channels;
import faang.school.promotionservice.dto.StockAlertDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockAlertEventPublisher extends AbstractEventPublisher<List<StockAlertDto>> {
    private final Channels channels;

    public StockAlertEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(List<StockAlertDto> stockAlertDto) {
        handleEvent(stockAlertDto, channels.getStockAlertChannel());
    }
}
