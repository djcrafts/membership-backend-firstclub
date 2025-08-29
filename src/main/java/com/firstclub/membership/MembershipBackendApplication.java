package com.firstclub.membership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for FirstClub Membership Backend
 * 
 * This application provides a comprehensive membership management system with:
 * - Multiple membership plans (Monthly, Quarterly, Yearly)
 * - Tiered benefits system (Silver, Gold, Platinum)
 * - Subscription management (subscribe, upgrade, downgrade, cancel)
 * - Configurable benefits and criteria
 * 
 * @author FirstClub Development Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class MembershipBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MembershipBackendApplication.class, args);
    }
}