package com.firstclub.membership.exception;

/**
 * Exception thrown when user is not found
 */
public class UserNotFoundException extends MembershipException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
    
    public UserNotFoundException(String identifier, String type) {
        super("User not found with " + type + ": " + identifier);
    }
}
