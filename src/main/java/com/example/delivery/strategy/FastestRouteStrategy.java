package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy extends AbstractRouteStrategy {

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        return getEligibleRoutes(routes, request).stream()
                .min(Comparator.comparingInt(Route::estimatedMinutes)
                        .thenComparing(
                                Comparator.comparingInt(Route::safetyScore).reversed())
                        .thenComparingDouble(this::calculateTotalCost))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Fastest";
    }
    @Override
    public String explainSelection(Route route) {
        return "Lowest estimated delivery time; safety and total cost break ties.";
    }
}