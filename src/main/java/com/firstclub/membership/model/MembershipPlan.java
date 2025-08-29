package com.firstclub.membership.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a membership plan (Monthly, Quarterly, Yearly)
 * 
 * This class defines the structure for different membership plans that users can subscribe to.
 * Each plan has a duration, pricing, and can be associated with multiple tiers.
 */
@Entity
@Table(name = "membership_plans")
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name cannot be blank")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Plan type cannot be blank")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    @NotNull(message = "Duration in months cannot be null")
    @Positive(message = "Duration must be positive")
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public MembershipPlan() {}

    public MembershipPlan(String name, PlanType planType, Integer durationMonths, BigDecimal price, String description) {
        this.name = name;
        this.planType = planType;
        this.durationMonths = durationMonths;
        this.price = price;
        this.description = description;
        this.isActive = true;
    }

    // Builder pattern
    public static MembershipPlanBuilder builder() {
        return new MembershipPlanBuilder();
    }

    public static class MembershipPlanBuilder {
        private String name;
        private PlanType planType;
        private Integer durationMonths;
        private BigDecimal price;
        private String description;
        private Boolean isActive = true;

        public MembershipPlanBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MembershipPlanBuilder planType(PlanType planType) {
            this.planType = planType;
            return this;
        }

        public MembershipPlanBuilder durationMonths(Integer durationMonths) {
            this.durationMonths = durationMonths;
            return this;
        }

        public MembershipPlanBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public MembershipPlanBuilder description(String description) {
            this.description = description;
            return this;
        }

        public MembershipPlanBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public MembershipPlan build() {
            MembershipPlan plan = new MembershipPlan();
            plan.name = this.name;
            plan.planType = this.planType;
            plan.durationMonths = this.durationMonths;
            plan.price = this.price;
            plan.description = this.description;
            plan.isActive = this.isActive;
            return plan;
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

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
     * Enum defining available plan types
     */
    public enum PlanType {
        MONTHLY("Monthly Plan", 1),
        QUARTERLY("Quarterly Plan", 3),
        YEARLY("Yearly Plan", 12);

        private final String displayName;
        private final int months;

        PlanType(String displayName, int months) {
            this.displayName = displayName;
            this.months = months;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMonths() {
            return months;
        }
    }
}