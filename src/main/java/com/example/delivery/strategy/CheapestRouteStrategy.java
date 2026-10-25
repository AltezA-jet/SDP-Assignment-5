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
    @Override
    public String explainSelection(Route route) {
        double totalCost = calculateTotalCost(route);

        return String.format(
                java.util.Locale.US,
                "Selected %s because it has the lowest total cost: "
                        + "%.2f (base cost %.2f + distance charge %.2f).",
                route.id(),
                totalCost,
                route.cost(),
                route.distanceKm() * 0.15
        );
    }
}