package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.Comparator;
import java.util.List;

public class PromiseKeeperRouteStrategy extends AbstractRouteStrategy {

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request) {

        return getEligibleRoutes(routes, request).stream()
                .min(
                        Comparator.comparingDouble(Route::onTimeRate)
                                .reversed()
                                .thenComparing(
                                        Comparator.comparingInt(
                                                Route::safetyScore
                                        ).reversed()
                                )
                                .thenComparingInt(Route::estimatedMinutes)
                                .thenComparingDouble(this::calculateTotalCost)
                )
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "PromiseKeeper";
    }
    @Override
    public String explainSelection(Route route) {
        return String.format(
                java.util.Locale.US,
                "Selected %s because %.1f%% of historical deliveries "
                        + "on this route arrived on time. "
                        + "Safety, time and cost are tie-breakers.",
                route.id(),
                route.onTimeRate()
        );
    }
}