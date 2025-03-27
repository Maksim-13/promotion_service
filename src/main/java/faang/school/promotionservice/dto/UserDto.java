package faang.school.promotionservice.dto;

import lombok.Builder;

@Builder
public record UserDto(
    Long id,
    String title,
    String description,
    String aboutMe,
    String username,
    String country,
    String city,
    String email
) {
}
