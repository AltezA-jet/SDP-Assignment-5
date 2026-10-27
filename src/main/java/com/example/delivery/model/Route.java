package com.example.delivery.model;

import java.util.Objects;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double cost,
        double co2Kg,
        boolean available,
        int safetyScore,
        double onTimeRate,
        RouteType routeType,
        TrafficLevel trafficLevel
) {
    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be empty");
        }

        Objects.requireNonNull(origin, "Origin cannot be null");
        Objects.requireNonNull(destination, "Destination cannot be null");
        Objects.requireNonNull(routeType, "Route type cannot be null");
        Objects.requireNonNull(trafficLevel, "Traffic level cannot be null");

        if (!Double.isFinite(distanceKm) || distanceKm <= 0
                || estimatedMinutes <= 0
                || !Double.isFinite(cost) || cost < 0
                || !Double.isFinite(co2Kg) || co2Kg < 0) {
            throw new IllegalArgumentException("Invalid route metrics");
        }

        if (safetyScore < 0 || safetyScore > 100) {
            throw new IllegalArgumentException("Safety score must be 0-100");
        }

        if (!Double.isFinite(onTimeRate)
                || onTimeRate < 0 || onTimeRate > 100) {
            throw new IllegalArgumentException("On-time rate must be 0-100");
        }
    }

    // Backward-compatible constructor for existing code and tests.
    public Route(
            String id,
            Location origin,
            Location destination,
            double distanceKm,
            int estimatedMinutes,
            double cost,
            double co2Kg,
            boolean available,
            int safetyScore,
            double onTimeRate) {

        this(
                id,
                origin,
                destination,
                distanceKm,
                estimatedMinutes,
                cost,
                co2Kg,
                available,
                safetyScore,
                onTimeRate,
                RouteType.CITY_STREET,
                TrafficLevel.MEDIUM
        );
    }
}