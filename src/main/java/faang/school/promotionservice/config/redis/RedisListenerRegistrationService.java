package faang.school.promotionservice.config.redis;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class RedisListenerRegistrationService {

    public void registerListener(
            RedisMessageListenerContainer container,
            MessageListener listener,
            String channelOrPattern
    ) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.addMessageListener(adapter, new PatternTopic(channelOrPattern));
    }
}
