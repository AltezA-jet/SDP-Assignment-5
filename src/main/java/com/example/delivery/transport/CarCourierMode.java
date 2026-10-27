package com.example.delivery.transport;

import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;

public class CarCourierMode implements TransportMode {

    @Override
    public String getName() {
        return "Car courier";
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
            case CITY_STREET -> 28.0;
            case HIGHWAY -> 85.0;
            case MIXED_ROAD -> 42.0;
            default -> 1.0;
        };
    }

    @Override
    public double costPerKm() {
        return 1.10;
    }

    @Override
    public double co2PerKm() {
        return 0.18;
    }

    @Override
    public double trafficMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.30;
            case HIGH -> 1.80;
        };
    }

    @Override
    public double emissionMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.15;
            case HIGH -> 1.45;
        };
    }

    @Override
    public int safetyAdjustment(Route route) {
        return switch (route.routeType()) {
            case CITY_STREET -> 0;
            case HIGHWAY -> 4;
            case MIXED_ROAD -> -5;
            default -> -15;
        };
    }

    @Override
    public double reliabilityPenalty(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 7.0;
            case HIGH -> 20.0;
        };
    }
}