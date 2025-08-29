package com.firstclub.membership.exception;

/**
 * Base exception for all membership-related errors
 */
public class MembershipException extends RuntimeException {
    
    public MembershipException(String message) {
        super(message);
    }
    
    public MembershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
