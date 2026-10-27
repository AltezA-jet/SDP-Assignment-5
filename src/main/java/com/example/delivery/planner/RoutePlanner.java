package com.example.delivery.planner;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.strategy.RouteStrategy;
import com.example.delivery.transport.TransportMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutePlanner {

    private RouteStrategy strategy;
    private TransportMode transportMode;

    // Preserves the existing constructor and behavior.
    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
        this.transportMode = null;
    }

    // New constructor combines a route-selection strategy and transport.
    public RoutePlanner(
            RouteStrategy strategy,
            TransportMode transportMode) {

        this.strategy = Objects.requireNonNull(strategy);
        this.transportMode = Objects.requireNonNull(transportMode);
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = Objects.requireNonNull(transportMode);
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public RouteDecision plan(
            List<Route> routes,
            DeliveryRequest request) {

        Objects.requireNonNull(request, "Request cannot be null");

        List<Route> evaluatedRoutes = transportMode == null
                ? routes
                : transportMode.adaptRoutes(routes);

        Route selected = strategy.selectRoute(evaluatedRoutes, request);

        List<String> rejectedReasons =
                explainRejectedRoutes(evaluatedRoutes, request, selected);

        String explanation = strategy.explainSelection(selected);

        if (transportMode != null) {
            explanation = "Transport: " + transportMode.getName()
                    + ". " + explanation;
        }

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

        List<String> eligibleButNotSelected = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        List<String> reasons = new ArrayList<>();

        boolean ignoredNullRoute = false;

        if (routes == null) {
            return reasons;
        }

        for (Route route : routes) {
            if (route == null) {
                ignoredNullRoute = true;
                continue;
            }

            if (route.id().equals(selected.id())) {
                continue;
            }

            List<String> problems = new ArrayList<>();

            if (transportMode != null && !transportMode.supports(route)) {
                problems.add(
                        "not suitable for " + transportMode.getName()
                );
            } else if (!route.available()) {
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
                eligibleButNotSelected.add(route.id());
            } else {
                rejected.add(
                        "Route " + route.id() + ": "
                                + String.join(", ", problems) + "."
                );
            }
        }

        if (!eligibleButNotSelected.isEmpty()) {
            reasons.add(
                    "Routes " + String.join(", ", eligibleButNotSelected)
                            + " met all constraints but ranked below "
                            + selected.id() + " under the "
                            + strategy.getStrategyName() + " strategy."
            );
        }

        reasons.addAll(rejected);

        if (ignoredNullRoute) {
            reasons.add("Null route entries were ignored.");
        }

        return reasons;
    }

    private double calculateTotalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }
}