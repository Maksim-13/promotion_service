package faang.school.promotionservice.entity.db;


import faang.school.promotionservice.enums.PromotionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private PromotionStatus promotionStatus;

    @Column(name = "total_views", nullable = false)
    private Integer totalViews;

    @Column(name = "remaining_views", nullable = false)
    private Integer remainingViews;

    @Column(name = "monetary_asset", nullable = false, precision = 19, scale = 4)
    private BigDecimal monetaryAsset;

    @Column(name = "notification_sent", nullable = false)
    private boolean notificationSent = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
}