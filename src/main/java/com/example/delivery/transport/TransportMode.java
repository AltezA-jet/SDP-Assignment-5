package com.example.delivery.transport;

import com.example.delivery.model.Route;
import com.example.delivery.model.TrafficLevel;

import java.util.ArrayList;
import java.util.List;

public interface TransportMode {

    String getName();

    boolean supports(Route route);

    double averageSpeedKmh(Route route);

    double costPerKm();

    double co2PerKm();

    double trafficMultiplier(Route route);

    double emissionMultiplier(Route route);

    int safetyAdjustment(Route route);

    double reliabilityPenalty(Route route);

    default Route adaptRoute(Route route) {
        boolean compatible = supports(route);

        if (!compatible) {
            return new Route(
                    route.id(),
                    route.origin(),
                    route.destination(),
                    route.distanceKm(),
                    route.estimatedMinutes(),
                    route.cost(),
                    route.co2Kg(),
                    false,
                    route.safetyScore(),
                    route.onTimeRate(),
                    route.routeType(),
                    route.trafficLevel()
            );
        }

        int estimatedMinutes = Math.max(
                1,
                (int) Math.ceil(
                        route.distanceKm() / averageSpeedKmh(route)
                                * 60.0 * trafficMultiplier(route)
                )
        );

        double adjustedCost =
                route.cost() + route.distanceKm() * costPerKm();

        double adjustedCo2 =
                route.distanceKm() * co2PerKm()
                        * emissionMultiplier(route);

        int safetyScore = Math.max(
                0,
                Math.min(100, route.safetyScore() + safetyAdjustment(route))
        );

        double onTimeRate = Math.max(
                0.0,
                Math.min(100.0,
                        route.onTimeRate() - reliabilityPenalty(route))
        );

        return new Route(
                route.id(),
                route.origin(),
                route.destination(),
                route.distanceKm(),
                estimatedMinutes,
                adjustedCost,
                adjustedCo2,
                route.available(),
                safetyScore,
                onTimeRate,
                route.routeType(),
                route.trafficLevel()
        );
    }

    default List<Route> adaptRoutes(List<Route> routes) {
        if (routes == null) {
            return null;
        }

        List<Route> adaptedRoutes = new ArrayList<>();

        for (Route route : routes) {
            adaptedRoutes.add(route == null ? null : adaptRoute(route));
        }

        return adaptedRoutes;
    }

    default double defaultEmissionMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.05;
            case HIGH -> 1.20;
        };
    }

    default double defaultReliabilityPenalty(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 3.0;
            case HIGH -> 8.0;
        };
    }
}