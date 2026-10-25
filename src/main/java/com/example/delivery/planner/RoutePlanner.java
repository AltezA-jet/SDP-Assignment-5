package com.example.delivery.planner;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.strategy.RouteStrategy;

import java.util.List;
import java.util.Objects;

public class RoutePlanner {

    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public RouteDecision plan(
            List<Route> routes,
            DeliveryRequest request) {

        Route selected = strategy.selectRoute(routes, request);

        return new RouteDecision(
                selected,
                strategy.getStrategyName(),
                "Route selected using the " + strategy.getStrategyName()
                        + " strategy. Route ID: " + selected.id()
        );
    }
}