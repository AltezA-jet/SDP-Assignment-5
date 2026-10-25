package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.List;

public interface RouteStrategy {

    Route selectRoute(List<Route> routes, DeliveryRequest request);

    String getStrategyName();

    default String explainSelection(Route route) {
        return "Route " + route.id() + " selected by "
                + getStrategyName() + " strategy.";
    }
}