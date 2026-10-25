package com.example.delivery.comparison;

import com.example.delivery.exception.NoSuitableRouteException;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.strategy.RouteStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RouteComparison {

    private final List<RouteStrategy> strategies;

    public RouteComparison(List<RouteStrategy> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one strategy is required"
            );
        }

        if (strategies.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Strategy list cannot contain null"
            );
        }

        this.strategies = List.copyOf(strategies);
    }

    public Map<String, String> compare(
            List<Route> routes,
            DeliveryRequest request) {

        Map<String, String> results = new LinkedHashMap<>();

        for (RouteStrategy strategy : strategies) {
            String name = strategy.getStrategyName();

            try {
                Route route = strategy.selectRoute(routes, request);

                String result = strategy.explainSelection(route)
                        + "\nDistance: " + route.distanceKm() + " km"
                        + "\nEstimated time: " + route.estimatedMinutes() + " min"
                        + "\nBase cost: " + route.cost()
                        + "\nTotal cost: " + calculateTotalCost(route)
                        + "\nCO2 emissions: " + route.co2Kg() + " kg"
                        + "\nSafety score: " + route.safetyScore() + "/100"
                        + "\nHistorical on-time rate: "
                        + route.onTimeRate() + "%";

                results.put(name, result);

            } catch (NoSuitableRouteException exception) {
                results.put(
                        name,
                        "No suitable route: " + exception.getMessage()
                );
            }
        }

        return results;
    }

    private double calculateTotalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }
}