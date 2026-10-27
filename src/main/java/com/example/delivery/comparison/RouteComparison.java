package com.example.delivery.comparison;

import com.example.delivery.exception.NoSuitableRouteException;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.strategy.RouteStrategy;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RouteComparison {

    private final List<RouteStrategy> strategies;

    public RouteComparison(List<RouteStrategy> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one strategy is required"
            );
        }

        if (strategies.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(
                    "Strategy list cannot contain null"
            );
        }

        this.strategies = List.copyOf(strategies);
    }

    public Map<String, String> compare(
            List<Route> routes,
            DeliveryRequest request) {

        Objects.requireNonNull(request, "Request cannot be null");

        Map<String, String> results = new LinkedHashMap<>();

        for (RouteStrategy strategy : strategies) {
            String name = strategy.getStrategyName();

            try {
                Route route = strategy.selectRoute(routes, request);
                results.put(name, formatResult(strategy, route));

            } catch (NoSuitableRouteException exception) {
                results.put(
                        name,
                        "No suitable route found.\n"
                                + "Reason: " + exception.getMessage()
                );
            }
        }

        return results;
    }

    private String formatResult(RouteStrategy strategy, Route route) {
        return "Selected route: " + route.id()
                + "\nReason: " + strategy.explainSelection(route)
                + String.format(
                        Locale.US,
                        "\nRoute type: %s"
                                + "\nTraffic: %s"
                                + "\nDistance: %.1f km"
                                + "\nDelivery time: %d min"
                                + "\nTransport-adjusted cost: %.2f"
                                + "\nTotal calculated cost: %.2f"
                                + "\nCO2 emissions: %.2f kg",
                        route.routeType(),
                        route.trafficLevel(),
                        route.distanceKm(),
                        route.estimatedMinutes(),
                        route.cost(),
                        calculateTotalCost(route),
                        route.co2Kg()
                )
                + "\nSafety score: " + route.safetyScore() + "/100"
                + String.format(
                        Locale.US,
                        "\nHistorical on-time rate: %.1f%%",
                        route.onTimeRate()
                );
    }

    private double calculateTotalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }
}