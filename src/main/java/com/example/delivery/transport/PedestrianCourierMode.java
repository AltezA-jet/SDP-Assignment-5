package com.example.delivery.transport;

import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;

public class PedestrianCourierMode implements TransportMode {

    @Override
    public String getName() {
        return "Pedestrian courier";
    }

    @Override
    public boolean supports(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH, CITY_STREET, MIXED_ROAD -> true;
            case BIKE_LANE, HIGHWAY -> false;
        };
    }

    @Override
    public double averageSpeedKmh(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH -> 5.5;
            case CITY_STREET -> 4.5;
            case MIXED_ROAD -> 3.8;
            default -> 4.0;
        };
    }

    @Override
    public double costPerKm() {
        return 0.20;
    }

    @Override
    public double co2PerKm() {
        return 0.0;
    }

    @Override
    public double trafficMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.02;
            case HIGH -> 1.05;
        };
    }

    @Override
    public double emissionMultiplier(Route route) {
        return 1.0;
    }

    @Override
    public int safetyAdjustment(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH -> 8;
            case CITY_STREET -> 0;
            case MIXED_ROAD -> -8;
            default -> -5;
        };
    }

    @Override
    public double reliabilityPenalty(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 2.0;
            case HIGH -> 4.0;
        };
    }
}