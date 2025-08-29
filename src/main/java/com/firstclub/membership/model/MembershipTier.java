package com.firstclub.membership.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing membership tiers (Silver, Gold, Platinum)
 * 
 * This class defines the tier system where users progress through different levels
 * based on their activity, spending, and other criteria.
 */
@Entity
@Table(name = "membership_tiers")
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tier name cannot be blank")
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull(message = "Tier level cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TierLevel level;

    @PositiveOrZero(message = "Minimum orders must be non-negative")
    @Column(name = "min_orders_required")
    private Integer minOrdersRequired = 0;

    @PositiveOrZero(message = "Minimum order value must be non-negative")
    @Column(name = "min_order_value_monthly", precision = 10, scale = 2)
    private BigDecimal minOrderValueMonthly = BigDecimal.ZERO;

    @PositiveOrZero(message = "Discount percentage must be non-negative")
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(name = "free_delivery", nullable = false)
    private Boolean freeDelivery = false;

    @Column(name = "priority_support", nullable = false)
    private Boolean prioritySupport = false;

    @Column(name = "exclusive_deals", nullable = false)
    private Boolean exclusiveDeals = false;

    @Column(name = "early_access", nullable = false)
    private Boolean earlyAccess = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "tier_cohorts", joinColumns = @JoinColumn(name = "tier_id"))
    @Column(name = "cohort_name")
    private List<String> eligibleCohorts;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public MembershipTier() {}

    public MembershipTier(String name, TierLevel level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
        this.minOrdersRequired = 0;
        this.minOrderValueMonthly = BigDecimal.ZERO;
        this.discountPercentage = BigDecimal.ZERO;
        this.freeDelivery = false;
        this.prioritySupport = false;
        this.exclusiveDeals = false;
        this.earlyAccess = false;
        this.isActive = true;
    }

    // Builder pattern
    public static MembershipTierBuilder builder() {
        return new MembershipTierBuilder();
    }

    public static class MembershipTierBuilder {
        private String name;
        private TierLevel level;
        private Integer minOrdersRequired = 0;
        private BigDecimal minOrderValueMonthly = BigDecimal.ZERO;
        private BigDecimal discountPercentage = BigDecimal.ZERO;
        private Boolean freeDelivery = false;
        private Boolean prioritySupport = false;
        private Boolean exclusiveDeals = false;
        private Boolean earlyAccess = false;
        private Boolean isActive = true;
        private String description;
        private List<String> eligibleCohorts;

        public MembershipTierBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MembershipTierBuilder level(TierLevel level) {
            this.level = level;
            return this;
        }

        public MembershipTierBuilder minOrdersRequired(Integer minOrdersRequired) {
            this.minOrdersRequired = minOrdersRequired;
            return this;
        }

        public MembershipTierBuilder minOrderValueMonthly(BigDecimal minOrderValueMonthly) {
            this.minOrderValueMonthly = minOrderValueMonthly;
            return this;
        }

        public MembershipTierBuilder discountPercentage(BigDecimal discountPercentage) {
            this.discountPercentage = discountPercentage;
            return this;
        }

        public MembershipTierBuilder freeDelivery(Boolean freeDelivery) {
            this.freeDelivery = freeDelivery;
            return this;
        }

        public MembershipTierBuilder prioritySupport(Boolean prioritySupport) {
            this.prioritySupport = prioritySupport;
            return this;
        }

        public MembershipTierBuilder exclusiveDeals(Boolean exclusiveDeals) {
            this.exclusiveDeals = exclusiveDeals;
            return this;
        }

        public MembershipTierBuilder earlyAccess(Boolean earlyAccess) {
            this.earlyAccess = earlyAccess;
            return this;
        }

        public MembershipTierBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public MembershipTierBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MembershipTierBuilder eligibleCohorts(List<String> eligibleCohorts) {
            this.eligibleCohorts = eligibleCohorts;
            return this;
        }

        public MembershipTier build() {
            MembershipTier tier = new MembershipTier();
            tier.name = this.name;
            tier.level = this.level;
            tier.minOrdersRequired = this.minOrdersRequired;
            tier.minOrderValueMonthly = this.minOrderValueMonthly;
            tier.discountPercentage = this.discountPercentage;
            tier.freeDelivery = this.freeDelivery;
            tier.prioritySupport = this.prioritySupport;
            tier.exclusiveDeals = this.exclusiveDeals;
            tier.earlyAccess = this.earlyAccess;
            tier.isActive = this.isActive;
            tier.description = this.description;
            tier.eligibleCohorts = this.eligibleCohorts;
            return tier;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TierLevel getLevel() {
        return level;
    }

    public void setLevel(TierLevel level) {
        this.level = level;
    }

    public Integer getMinOrdersRequired() {
        return minOrdersRequired;
    }

    public void setMinOrdersRequired(Integer minOrdersRequired) {
        this.minOrdersRequired = minOrdersRequired;
    }

    public BigDecimal getMinOrderValueMonthly() {
        return minOrderValueMonthly;
    }

    public void setMinOrderValueMonthly(BigDecimal minOrderValueMonthly) {
        this.minOrderValueMonthly = minOrderValueMonthly;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Boolean getFreeDelivery() {
        return freeDelivery;
    }

    public void setFreeDelivery(Boolean freeDelivery) {
        this.freeDelivery = freeDelivery;
    }

    public Boolean getPrioritySupport() {
        return prioritySupport;
    }

    public void setPrioritySupport(Boolean prioritySupport) {
        this.prioritySupport = prioritySupport;
    }

    public Boolean getExclusiveDeals() {
        return exclusiveDeals;
    }

    public void setExclusiveDeals(Boolean exclusiveDeals) {
        this.exclusiveDeals = exclusiveDeals;
    }

    public Boolean getEarlyAccess() {
        return earlyAccess;
    }

    public void setEarlyAccess(Boolean earlyAccess) {
        this.earlyAccess = earlyAccess;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEligibleCohorts() {
        return eligibleCohorts;
    }

    public void setEligibleCohorts(List<String> eligibleCohorts) {
        this.eligibleCohorts = eligibleCohorts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum defining tier levels in ascending order
     */
    public enum TierLevel {
        SILVER(1, "Silver Tier"),
        GOLD(2, "Gold Tier"),
        PLATINUM(3, "Platinum Tier");

        private final int priority;
        private final String displayName;

        TierLevel(int priority, String displayName) {
            this.priority = priority;
            this.displayName = displayName;
        }

        public int getPriority() {
            return priority;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}