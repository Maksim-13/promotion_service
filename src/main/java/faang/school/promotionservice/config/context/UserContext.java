package faang.school.promotionservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        if (userIdHolder.get() != null) {
            return userIdHolder.get();
        }
        return 1L;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
