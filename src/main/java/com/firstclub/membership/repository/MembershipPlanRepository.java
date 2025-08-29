package com.firstclub.membership.repository;

import com.firstclub.membership.model.MembershipPlan;
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
 * Repository interface for MembershipPlan entity operations
 * 
 * Provides comprehensive data access methods for membership plan management.
 */
@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    // Basic plan queries
    List<MembershipPlan> findByIsActiveTrue();
    
    Optional<MembershipPlan> findByName(String name);
    
    List<MembershipPlan> findByPlanType(MembershipPlan.PlanType planType);
    
    // Plan search and filtering
    @Query("SELECT mp FROM MembershipPlan mp WHERE mp.isActive = true AND " +
           "(:planType IS NULL OR mp.planType = :planType) AND " +
           "(:minPrice IS NULL OR mp.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR mp.price <= :maxPrice)")
    Page<MembershipPlan> findActivePlansFiltered(
        @Param("planType") MembershipPlan.PlanType planType,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable);
    
    // Price range queries
    List<MembershipPlan> findByPriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<MembershipPlan> findByPriceLessThanEqualAndIsActiveTrue(BigDecimal maxPrice);
    
    // Duration queries
    List<MembershipPlan> findByDurationMonthsAndIsActiveTrue(Integer durationMonths);
    
    List<MembershipPlan> findByDurationMonthsLessThanEqualAndIsActiveTrue(Integer maxDurationMonths);
    
    // Statistics queries
    @Query("SELECT COUNT(mp) FROM MembershipPlan mp WHERE mp.isActive = true")
    long countActivePlans();
    
    @Query("SELECT AVG(mp.price) FROM MembershipPlan mp WHERE mp.isActive = true")
    BigDecimal getAveragePrice();
    
    @Query("SELECT MIN(mp.price) FROM MembershipPlan mp WHERE mp.isActive = true")
    BigDecimal getMinPrice();
    
    @Query("SELECT MAX(mp.price) FROM MembershipPlan mp WHERE mp.isActive = true")
    BigDecimal getMaxPrice();
    
    // Plan type statistics
    @Query("SELECT mp.planType, COUNT(mp) FROM MembershipPlan mp WHERE mp.isActive = true GROUP BY mp.planType")
    List<Object[]> getPlanTypeStatistics();
}