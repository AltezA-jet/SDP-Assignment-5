package com.example.delivery.planner;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutePlanner {

    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public RouteDecision plan(
            List<Route> routes,
            DeliveryRequest request) {

        Objects.requireNonNull(request, "Request cannot be null");

        Route selected = strategy.selectRoute(routes, request);

        List<String> rejectedReasons =
                explainRejectedRoutes(routes, request, selected);

        String explanation = "Route " + selected.id()
                + " selected using " + strategy.getStrategyName()
                + " strategy. Estimated time: "
                + selected.estimatedMinutes() + " minutes, total cost: "
                + calculateTotalCost(selected)
                + ", emissions: " + selected.co2Kg() + " kg.";

        return new RouteDecision(
                selected,
                strategy.getStrategyName(),
                explanation,
                rejectedReasons
        );
    }

    private List<String> explainRejectedRoutes(
            List<Route> routes,
            DeliveryRequest request,
            Route selected) {

        List<String> reasons = new ArrayList<>();

        if (routes == null) {
            return reasons;
        }

        for (Route route : routes) {
            if (route == null) {
                reasons.add("A null route was ignored.");
                continue;
            }

            if (route.id().equals(selected.id())) {
                continue;
            }

            List<String> problems = new ArrayList<>();

            if (!route.available()) {
                problems.add("route is unavailable");
            }

            if (!route.origin().equals(request.origin())) {
                problems.add("origin does not match");
            }

            if (!route.destination().equals(request.destination())) {
                problems.add("destination does not match");
            }

            if (request.prohibitedRouteIds().contains(route.id())) {
                problems.add("route is prohibited");
            }

            if (calculateTotalCost(route) > request.maxBudget()) {
                problems.add("total cost exceeds budget");
            }

            if (route.estimatedMinutes() > request.maxDeliveryMinutes()) {
                problems.add("delivery deadline exceeded");
            }

            if (route.co2Kg() > request.maxEmissionsKg()) {
                problems.add("emissions limit exceeded");
            }

            if (problems.isEmpty()) {
                reasons.add("Route " + route.id()
                        + " was not selected by the "
                        + strategy.getStrategyName()
                        + " strategy because another eligible route scored better.");
            } else {
                reasons.add("Route " + route.id() + " rejected: "
                        + String.join(", ", problems) + ".");
            }
        }

        return reasons;
    }

    private double calculateTotalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }
}