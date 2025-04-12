package faang.school.promotionservice.repository.db;


import faang.school.promotionservice.entity.db.Promotion;
import faang.school.promotionservice.enums.PromotionStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query(
            nativeQuery = true,
            value = """
            INSERT INTO promotion (user_id, total_views, start_date, end_date)
            VALUES (:userId, :totalViews, :startDate, :endDate)
            RETURNING *
            """
    )
    @Transactional
    Promotion create(
            @Param("userId") Long userId,
            @Param("totalViews") Integer totalViews,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate

    );

    @Modifying
    @Query("UPDATE Promotion p SET p.promotionStatus = :status, p.remainingViews = :viewsUsed WHERE p.id = :id")
    void updateStatusAndTotalViews(@Param("id") Long id, @Param("status") PromotionStatus status, @Param("viewsUsed") Long viewsUsed);

    @Query("SELECT p FROM Promotion p WHERE p.promotionStatus = :status " +
            "AND CURRENT_TIMESTAMP BETWEEN p.startDate AND p.endDate")
    List<Promotion> findPromotionsByStatusInDateRange(@Param("status") PromotionStatus status);

    List<Promotion> findByIdIn(List<Long> promoIds);

    @Query(value = """
    SELECT * FROM promotion p 
    WHERE p.notification_sent = false 
    AND (p.remaining_views < :remainingViewsThreshold OR p.end_date < :timeThreshold)
    """, nativeQuery = true)
    List<Promotion> findUserIdsWithLowRemainingViewsOrNearExpiry(
            @Param("remainingViewsThreshold") int remainingViewsThreshold,
            @Param("timeThreshold") LocalDateTime timeThreshold
    );

    @Modifying
    @Query("UPDATE Promotion p SET p.notificationSent = true WHERE p.id IN :ids")
    void markAsNotified(@Param("ids") List<Long> ids);
}
