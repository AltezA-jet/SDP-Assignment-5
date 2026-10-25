package com.example.delivery.strategy;

import com.example.delivery.exception.NoSuitableRouteException;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.List;

public abstract class AbstractRouteStrategy implements RouteStrategy {

    protected List<Route> getEligibleRoutes(
            List<Route> routes,
            DeliveryRequest request) {

        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException("No routes provided");
        }

        List<Route> eligible = routes.stream()
                .filter(route -> route != null)
                .filter(Route::available)
                .filter(route -> route.origin().equals(request.origin()))
                .filter(route -> route.destination().equals(request.destination()))
                .filter(route -> !request.prohibitedRouteIds().contains(route.id()))
                .filter(route -> calculateTotalCost(route) <= request.maxBudget())
                .filter(route -> route.estimatedMinutes() <= request.maxDeliveryMinutes())
                .filter(route -> route.co2Kg() <= request.maxEmissionsKg())
                .toList();

        if (eligible.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No route satisfies all delivery constraints");
        }

        return eligible;
    }

    protected double calculateTotalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }
}