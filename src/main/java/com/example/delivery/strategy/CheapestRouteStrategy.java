package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.Comparator;
import java.util.List;

public class CheapestRouteStrategy extends AbstractRouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        return getEligibleRoutes(routes, request).stream()
                .min(Comparator.comparingDouble(this::calculateTotalCost)
                        .thenComparingInt(Route::estimatedMinutes))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Cheapest";
    }
}