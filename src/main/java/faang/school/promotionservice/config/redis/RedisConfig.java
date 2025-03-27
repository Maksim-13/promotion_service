package faang.school.promotionservice.config.redis;


import faang.school.promotionservice.dto.CachePromoDto;
import faang.school.promotionservice.listener.RedisExpirationListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    public static final String KEY_EVENT_TTL_EXPIRED = "__keyevent@0__:expired";
    private final RedisProperties redisProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                redisProperties.getHost(),
                redisProperties.getPort());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer(
            RedisListenerRegistrationService registrationService,
            RedisExpirationListener expirationListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        registrationService.registerListener(
                container,
                expirationListener,
                KEY_EVENT_TTL_EXPIRED
        );
        return container;
    }

    @Bean
    public RedisTemplate<String, CachePromoDto> redisTemplatePromo() {
        RedisTemplate<String, CachePromoDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<CachePromoDto> serializer
                = new Jackson2JsonRedisSerializer<>(CachePromoDto.class);
        template.setValueSerializer(serializer);
        return template;
    }
}