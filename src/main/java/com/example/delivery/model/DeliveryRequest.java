package com.example.delivery.model;

import java.util.Set;

public record DeliveryRequest(
        Location origin,
        Location destination,
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        double maxEmissionsKg,
        Set<String> prohibitedRouteIds
) {
    public DeliveryRequest {
        if (origin == null || destination == null || priority == null) {
            throw new IllegalArgumentException("Request fields cannot be null");
        }
        if (Double.isNaN(maxBudget) || maxBudget < 0
                || maxDeliveryMinutes < 0
                || Double.isNaN(maxEmissionsKg) || maxEmissionsKg < 0) {
            throw new IllegalArgumentException("Invalid delivery constraints");
        }
        prohibitedRouteIds = prohibitedRouteIds == null
                ? Set.of()
                : Set.copyOf(prohibitedRouteIds);
    }
}