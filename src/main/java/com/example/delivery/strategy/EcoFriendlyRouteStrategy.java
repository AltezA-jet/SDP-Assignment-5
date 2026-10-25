package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.Comparator;
import java.util.List;

public class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {

    private final double emissionWeight;
    private final double distanceWeight;

    public EcoFriendlyRouteStrategy() {
        this(0.7, 0.3);
    }

    public EcoFriendlyRouteStrategy(
            double emissionWeight,
            double distanceWeight) {

        if (!Double.isFinite(emissionWeight)
                || !Double.isFinite(distanceWeight)
                || emissionWeight < 0
                || distanceWeight < 0
                || Math.abs(emissionWeight + distanceWeight - 1.0) > 1e-9) {
            throw new IllegalArgumentException(
                    "Weights must be non-negative and sum to 1.0");
        }

        this.emissionWeight = emissionWeight;
        this.distanceWeight = distanceWeight;
    }

    @Override
    public Route selectRoute(List<Route> routes, DeliveryRequest request) {
        return getEligibleRoutes(routes, request).stream()
                .min(Comparator.comparingDouble(this::ecoScore))
                .orElseThrow();
    }

    private double ecoScore(Route route) {
        return route.co2Kg() * emissionWeight
                + route.distanceKm() * distanceWeight;
    }

    @Override
    public String getStrategyName() {
        return "EcoFriendly";
    }
    @Override
    public String explainSelection(Route route) {
        double score = ecoScore(route);

        return String.format(
                java.util.Locale.US,
                "Selected %s because it has the lowest eco score: %.2f "
                        + "(CO2: %.2f kg, distance: %.2f km).",
                route.id(),
                score,
                route.co2Kg(),
                route.distanceKm()
        );
    }
}