package com.example.delivery.model;

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
        double onTimeRate
) {
    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be empty");
        }

        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

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
}