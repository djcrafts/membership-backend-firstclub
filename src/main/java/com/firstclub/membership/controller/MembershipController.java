package com.firstclub.membership.controller;

import com.firstclub.membership.model.MembershipPlan;
import com.firstclub.membership.model.MembershipTier;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * REST controller for FirstClub membership operations
 */
@RestController
@RequestMapping("/memberships")
@CrossOrigin(origins = "*")
public class MembershipController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "FirstClub Membership Backend");
        response.put("message", "Service is running successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plans")
    public ResponseEntity<List<Map<String, Object>>> getPlans() {
        List<Map<String, Object>> plans = new ArrayList<>();
        
        Map<String, Object> monthlyPlan = new HashMap<>();
        monthlyPlan.put("id", 1L);
        monthlyPlan.put("name", "Monthly Plan");
        monthlyPlan.put("planType", "MONTHLY");
        monthlyPlan.put("durationMonths", 1);
        monthlyPlan.put("price", new BigDecimal("99.99"));
        monthlyPlan.put("description", "Monthly subscription with basic benefits");
        plans.add(monthlyPlan);

        Map<String, Object> quarterlyPlan = new HashMap<>();
        quarterlyPlan.put("id", 2L);
        quarterlyPlan.put("name", "Quarterly Plan");
        quarterlyPlan.put("planType", "QUARTERLY");
        quarterlyPlan.put("durationMonths", 3);
        quarterlyPlan.put("price", new BigDecimal("279.99"));
        quarterlyPlan.put("description", "Quarterly subscription with enhanced benefits");
        plans.add(quarterlyPlan);

        Map<String, Object> yearlyPlan = new HashMap<>();
        yearlyPlan.put("id", 3L);
        yearlyPlan.put("name", "Yearly Plan");
        yearlyPlan.put("planType", "YEARLY");
        yearlyPlan.put("durationMonths", 12);
        yearlyPlan.put("price", new BigDecimal("999.99"));
        yearlyPlan.put("description", "Yearly subscription with premium benefits and savings");
        plans.add(yearlyPlan);

        return ResponseEntity.ok(plans);
    }

    @GetMapping("/tiers")
    public ResponseEntity<List<Map<String, Object>>> getTiers() {
        List<Map<String, Object>> tiers = new ArrayList<>();
        
        Map<String, Object> silverTier = new HashMap<>();
        silverTier.put("id", 1L);
        silverTier.put("name", "Silver");
        silverTier.put("level", "SILVER");
        silverTier.put("minOrdersRequired", 0);
        silverTier.put("minOrderValueMonthly", new BigDecimal("0.00"));
        silverTier.put("discountPercentage", new BigDecimal("5.00"));
        silverTier.put("freeDelivery", false);
        silverTier.put("description", "Entry-level tier with basic benefits");
        tiers.add(silverTier);

        Map<String, Object> goldTier = new HashMap<>();
        goldTier.put("id", 2L);
        goldTier.put("name", "Gold");
        goldTier.put("level", "GOLD");
        goldTier.put("minOrdersRequired", 5);
        goldTier.put("minOrderValueMonthly", new BigDecimal("200.00"));
        goldTier.put("discountPercentage", new BigDecimal("10.00"));
        goldTier.put("freeDelivery", true);
        goldTier.put("description", "Mid-level tier with enhanced benefits and free delivery");
        tiers.add(goldTier);

        Map<String, Object> platinumTier = new HashMap<>();
        platinumTier.put("id", 3L);
        platinumTier.put("name", "Platinum");
        platinumTier.put("level", "PLATINUM");
        platinumTier.put("minOrdersRequired", 10);
        platinumTier.put("minOrderValueMonthly", new BigDecimal("500.00"));
        platinumTier.put("discountPercentage", new BigDecimal("20.00"));
        platinumTier.put("freeDelivery", true);
        platinumTier.put("description", "Premium tier with maximum benefits and exclusive access");
        tiers.add(platinumTier);

        return ResponseEntity.ok(tiers);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> createSubscription(
            @RequestParam String userId,
            @RequestParam String planId,
            @RequestParam String tierId) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Subscription created successfully");
        response.put("userId", userId);
        response.put("planId", planId);
        response.put("tierId", tierId);
        response.put("subscriptionId", "SUB_" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription/{userId}")
    public ResponseEntity<Map<String, Object>> getSubscription(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("userId", userId);
        response.put("subscriptionId", "SUB_123456789");
        response.put("planName", "Monthly Plan");
        response.put("tierName", "Silver");
        response.put("subscriptionStatus", "ACTIVE");
        response.put("startDate", "2024-01-01T00:00:00");
        response.put("expiresAt", "2024-02-01T00:00:00");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activity")
    public ResponseEntity<Map<String, String>> recordActivity(
            @RequestParam String userId,
            @RequestParam String activityType,
            @RequestParam(required = false, defaultValue = "1.0") String value) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Activity recorded successfully");
        response.put("userId", userId);
        response.put("activityType", activityType);
        response.put("value", value);
        response.put("activityId", "ACT_" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
