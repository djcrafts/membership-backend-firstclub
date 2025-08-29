package com.firstclub.membership.exception;

/**
 * Exception thrown when subscription-related operations fail
 */
public class SubscriptionException extends MembershipException {
    
    public SubscriptionException(String message) {
        super(message);
    }
    
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class ActiveSubscriptionExistsException extends SubscriptionException {
        public ActiveSubscriptionExistsException(String userId) {
            super("User " + userId + " already has an active subscription");
        }
    }
    
    public static class NoActiveSubscriptionException extends SubscriptionException {
        public NoActiveSubscriptionException(String userId) {
            super("No active subscription found for user: " + userId);
        }
    }
    
    public static class TierEligibilityException extends SubscriptionException {
        public TierEligibilityException(String userId, String tierName) {
            super("User " + userId + " does not meet requirements for " + tierName + " tier");
        }
    }
}
