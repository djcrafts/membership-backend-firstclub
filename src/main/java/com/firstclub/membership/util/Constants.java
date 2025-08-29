package com.firstclub.membership.util;

import java.math.BigDecimal;

/**
 * Application constants for membership management
 * 
 * This class contains all configurable constants used throughout the application
 * for business logic, validation, and default values.
 */
public final class Constants {

    // Private constructor to prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Default membership plan configurations
     */
    public static final class Plans {
        public static final String MONTHLY_PLAN_NAME = "Monthly Membership";
        public static final String QUARTERLY_PLAN_NAME = "Quarterly Membership";
        public static final String YEARLY_PLAN_NAME = "Yearly Membership";
        
        public static final BigDecimal MONTHLY_PLAN_PRICE = new BigDecimal("9.99");
        public static final BigDecimal QUARTERLY_PLAN_PRICE = new BigDecimal("27.99");
        public static final BigDecimal YEARLY_PLAN_PRICE = new BigDecimal("99.99");
        
        public static final int MONTHLY_DURATION = 1;
        public static final int QUARTERLY_DURATION = 3;
        public static final int YEARLY_DURATION = 12;
    }

    /**
     * Default membership tier configurations
     */
    public static final class Tiers {
        public static final String SILVER_TIER_NAME = "Silver Membership";
        public static final String GOLD_TIER_NAME = "Gold Membership";
        public static final String PLATINUM_TIER_NAME = "Platinum Membership";
        
        // Silver tier requirements
        public static final int SILVER_MIN_ORDERS = 0;
        public static final BigDecimal SILVER_MIN_VALUE = BigDecimal.ZERO;
        public static final BigDecimal SILVER_DISCOUNT = new BigDecimal("5.00");
        
        // Gold tier requirements
        public static final int GOLD_MIN_ORDERS = 5;
        public static final BigDecimal GOLD_MIN_VALUE = new BigDecimal("100.00");
        public static final BigDecimal GOLD_DISCOUNT = new BigDecimal("10.00");
        
        // Platinum tier requirements
        public static final int PLATINUM_MIN_ORDERS = 15;
        public static final BigDecimal PLATINUM_MIN_VALUE = new BigDecimal("500.00");
        public static final BigDecimal PLATINUM_DISCOUNT = new BigDecimal("15.00");
    }

    /**
     * Business rules and limits
     */
    public static final class BusinessRules {
        public static final int SUBSCRIPTION_EXPIRY_WARNING_DAYS = 7;
        public static final int MAX_SUBSCRIPTION_DURATION_YEARS = 5;
        public static final int ACTIVITY_RETENTION_MONTHS = 24;
        public static final int TIER_EVALUATION_FREQUENCY_HOURS = 6;
        
        // Concurrency limits
        public static final int MAX_CONCURRENT_TIER_EVALUATIONS = 10;
        public static final int MAX_ACTIVITY_BATCH_SIZE = 100;
        
        // Rate limiting
        public static final int SUBSCRIPTION_OPERATIONS_PER_USER_PER_HOUR = 5;
        public static final int ACTIVITY_RECORDS_PER_USER_PER_MINUTE = 20;
    }

    /**
     * Validation constraints
     */
    public static final class Validation {
        public static final int USER_ID_MIN_LENGTH = 3;
        public static final int USER_ID_MAX_LENGTH = 50;
        public static final int PLAN_NAME_MAX_LENGTH = 100;
        public static final int TIER_NAME_MAX_LENGTH = 100;
        public static final int DESCRIPTION_MAX_LENGTH = 1000;
        public static final int CANCELLATION_REASON_MAX_LENGTH = 500;
        
        public static final BigDecimal MAX_ORDER_VALUE = new BigDecimal("999999.99");
        public static final BigDecimal MIN_ORDER_VALUE = BigDecimal.ZERO;
        public static final int MAX_ORDER_COUNT = 10000;
    }

    /**
     * Default cohort configurations
     */
    public static final class Cohorts {
        public static final String PREMIUM_COHORT = "PREMIUM";
        public static final String STANDARD_COHORT = "STANDARD";
        public static final String VIP_COHORT = "VIP";
        public static final String ENTERPRISE_COHORT = "ENTERPRISE";
    }

    /**
     * Cache configurations
     */
    public static final class Cache {
        public static final String MEMBERSHIP_PLANS_CACHE = "membershipPlans";
        public static final String MEMBERSHIP_TIERS_CACHE = "membershipTiers";
        public static final String USER_SUBSCRIPTIONS_CACHE = "userSubscriptions";
        public static final String TIER_ELIGIBILITY_CACHE = "tierEligibility";
        
        public static final int DEFAULT_CACHE_TTL_SECONDS = 3600; // 1 hour
        public static final int PLANS_CACHE_TTL_SECONDS = 7200; // 2 hours
        public static final int TIERS_CACHE_TTL_SECONDS = 7200; // 2 hours
        public static final int SUBSCRIPTION_CACHE_TTL_SECONDS = 1800; // 30 minutes
    }

    /**
     * Error messages
     */
    public static final class ErrorMessages {
        public static final String USER_NOT_FOUND = "User not found";
        public static final String SUBSCRIPTION_NOT_FOUND = "No active subscription found";
        public static final String PLAN_NOT_FOUND = "Membership plan not found";
        public static final String TIER_NOT_FOUND = "Membership tier not found";
        public static final String SUBSCRIPTION_ALREADY_EXISTS = "User already has an active subscription";
        public static final String TIER_NOT_ELIGIBLE = "User does not meet requirements for selected tier";
        public static final String INVALID_TIER_CHANGE = "Invalid tier change operation";
        public static final String SUBSCRIPTION_EXPIRED = "Subscription has expired";
        public static final String INVALID_ACTIVITY_TYPE = "Invalid activity type";
        public static final String OPERATION_NOT_ALLOWED = "Operation not allowed in current state";
    }

    /**
     * Success messages
     */
    public static final class SuccessMessages {
        public static final String SUBSCRIPTION_CREATED = "Subscription created successfully";
        public static final String SUBSCRIPTION_UPDATED = "Subscription updated successfully";
        public static final String SUBSCRIPTION_CANCELLED = "Subscription cancelled successfully";
        public static final String TIER_UPGRADED = "Tier upgraded successfully";
        public static final String TIER_DOWNGRADED = "Tier downgraded successfully";
        public static final String ACTIVITY_RECORDED = "Activity recorded successfully";
    }

    /**
     * API response codes
     */
    public static final class ResponseCodes {
        public static final String SUCCESS = "SUCCESS";
        public static final String ERROR = "ERROR";
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String NOT_FOUND = "NOT_FOUND";
        public static final String CONFLICT = "CONFLICT";
        public static final String UNAUTHORIZED = "UNAUTHORIZED";
    }

    /**
     * Feature flags
     */
    public static final class FeatureFlags {
        public static final boolean ENABLE_AUTO_TIER_UPGRADE = true;
        public static final boolean ENABLE_SUBSCRIPTION_RENEWAL = true;
        public static final boolean ENABLE_COHORT_BASED_TIERS = true;
        public static final boolean ENABLE_ACTIVITY_TRACKING = true;
        public static final boolean ENABLE_EARLY_ACCESS_FEATURES = true;
    }
}