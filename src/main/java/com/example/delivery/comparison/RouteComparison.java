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

        this.strategies = List.copyOf(strategies);
    }

    public Map<String, String> compare(
            List<Route> routes,
            DeliveryRequest request) {

        Map<String, String> results = new LinkedHashMap<>();

        for (RouteStrategy strategy : strategies) {
            String name = strategy.getStrategyName();

            try {
                Route selected = strategy.selectRoute(routes, request);

                results.put(
                        name,
                        "Selected route: " + selected.id()
                                + ", time: " + selected.estimatedMinutes()
                                + " min, cost: " + selected.cost()
                                + ", emissions: " + selected.co2Kg() + " kg"
                );

            } catch (NoSuitableRouteException exception) {
                results.put(name, "No suitable route: " + exception.getMessage());
            }
        }

        return results;
    }
}