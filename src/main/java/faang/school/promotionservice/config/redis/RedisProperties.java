package faang.school.promotionservice.config.redis;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
}
