package com.example.delivery.transport;

import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;

public class BicycleCourierMode implements TransportMode {

    @Override
    public String getName() {
        return "Bicycle courier";
    }

    @Override
    public boolean supports(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH, BIKE_LANE, CITY_STREET, MIXED_ROAD -> true;
            case HIGHWAY -> false;
        };
    }

    @Override
    public double averageSpeedKmh(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH -> 12.0;
            case BIKE_LANE -> 20.0;
            case CITY_STREET -> 15.0;
            case MIXED_ROAD -> 11.0;
            case HIGHWAY -> 1.0;
        };
    }

    @Override
    public double costPerKm() {
        return 0.35;
    }

    @Override
    public double co2PerKm() {
        return 0.015;
    }

    @Override
    public double trafficMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.10;
            case HIGH -> 1.35;
        };
    }

    @Override
    public double emissionMultiplier(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 1.05;
            case HIGH -> 1.15;
        };
    }

    @Override
    public int safetyAdjustment(Route route) {
        return switch (route.routeType()) {
            case PARK_PATH -> 3;
            case BIKE_LANE -> 8;
            case CITY_STREET -> -3;
            case MIXED_ROAD -> -10;
            case HIGHWAY -> -20;
        };
    }

    @Override
    public double reliabilityPenalty(Route route) {
        return switch (route.trafficLevel()) {
            case LOW -> 1.0;
            case MEDIUM -> 4.0;
            case HIGH -> 9.0;
        };
    }
}