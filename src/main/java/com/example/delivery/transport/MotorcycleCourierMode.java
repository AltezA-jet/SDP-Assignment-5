package com.example.delivery.transport;

import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;

public class MotorcycleCourierMode implements TransportMode {

    @Override
    public String getName() {
        return "Motorcycle courier";
    }

    @Override
    public boolean supports(Route route) {
        return switch (route.routeType()) {
            case CITY_STREET, HIGHWAY, MIXED_ROAD -> true;
            case PARK_PATH, BIKE_LANE -> false;
        };
    }

    @Override
    public double averageSpeedKmh(Route route) {
        return switch (route.routeType()) {
            case CITY_STREET -> 35.0;
            case HIGHWAY -> 75.0;
            case MIXED_ROAD -> 45.0;
            default -> 1.0;
        };
    }

    @Override
    public double costPerKm() {
        return 0.85;
    }

    @Override
    public double co2PerKm() {
        return 0.08;
    }

    @Override
    public double trafficMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.18;
            case HIGH -> 1.55;
        };
    }

    @Override
    public double emissionMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.08;
            case HIGH -> 1.25;
        };
    }

    @Override
    public int safetyAdjustment(Route route) {
        return switch (route.routeType()) {
            case CITY_STREET -> -3;
            case HIGHWAY -> 2;
            case MIXED_ROAD -> -8;
            default -> -15;
        };
    }

    @Override
    public double reliabilityPenalty(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 5.0;
            case HIGH -> 15.0;
        };
    }
}