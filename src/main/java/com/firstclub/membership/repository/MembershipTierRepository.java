package com.firstclub.membership.repository;

import com.firstclub.membership.model.MembershipTier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MembershipTier entity operations
 * 
 * Provides comprehensive data access methods for membership tier management.
 */
@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {

    // Basic tier queries
    List<MembershipTier> findByIsActiveTrue();
    
    Optional<MembershipTier> findByName(String name);
    
    List<MembershipTier> findByLevel(MembershipTier.TierLevel level);
    
    // Benefit-based queries
    List<MembershipTier> findByFreeDeliveryTrueAndIsActiveTrue();
    
    List<MembershipTier> findByPrioritySupportTrueAndIsActiveTrue();
    
    List<MembershipTier> findByEarlyAccessTrueAndIsActiveTrue();
    
    List<MembershipTier> findByExclusiveDealsTrueAndIsActiveTrue();
    
    // Discount queries
    List<MembershipTier> findByDiscountPercentageGreaterThanEqualAndIsActiveTrue(BigDecimal minDiscount);
    
    @Query("SELECT mt FROM MembershipTier mt WHERE mt.isActive = true AND " +
           "mt.discountPercentage BETWEEN :minDiscount AND :maxDiscount")
    List<MembershipTier> findByDiscountRange(@Param("minDiscount") BigDecimal minDiscount, 
                                           @Param("maxDiscount") BigDecimal maxDiscount);
    
    // Requirements-based queries
    List<MembershipTier> findByMinOrdersRequiredLessThanEqualAndIsActiveTrue(Integer maxOrders);
    
    List<MembershipTier> findByMinOrderValueMonthlyLessThanEqualAndIsActiveTrue(BigDecimal maxValue);
    
    // Complex filtering
    @Query("SELECT mt FROM MembershipTier mt WHERE mt.isActive = true AND " +
           "(:level IS NULL OR mt.level = :level) AND " +
           "(:minDiscount IS NULL OR mt.discountPercentage >= :minDiscount) AND " +
           "(:requiresFreeDelivery IS NULL OR mt.freeDelivery = :requiresFreeDelivery) AND " +
           "(:requiresPrioritySupport IS NULL OR mt.prioritySupport = :requiresPrioritySupport)")
    Page<MembershipTier> findTiersFiltered(
        @Param("level") MembershipTier.TierLevel level,
        @Param("minDiscount") BigDecimal minDiscount,
        @Param("requiresFreeDelivery") Boolean requiresFreeDelivery,
        @Param("requiresPrioritySupport") Boolean requiresPrioritySupport,
        Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(mt) FROM MembershipTier mt WHERE mt.isActive = true")
    long countActiveTiers();
    
    @Query("SELECT AVG(mt.discountPercentage) FROM MembershipTier mt WHERE mt.isActive = true")
    BigDecimal getAverageDiscount();
    
    @Query("SELECT MAX(mt.discountPercentage) FROM MembershipTier mt WHERE mt.isActive = true")
    BigDecimal getMaxDiscount();
    
    // Level-based statistics
    @Query("SELECT mt.level, COUNT(mt) FROM MembershipTier mt WHERE mt.isActive = true GROUP BY mt.level")
    List<Object[]> getTierLevelStatistics();
    
    // Cohort queries
    @Query("SELECT mt FROM MembershipTier mt JOIN mt.eligibleCohorts c WHERE c = :cohort AND mt.isActive = true")
    List<MembershipTier> findByCohort(@Param("cohort") String cohort);
}