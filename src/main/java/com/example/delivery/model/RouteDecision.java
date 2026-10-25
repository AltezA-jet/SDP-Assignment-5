package com.example.delivery.model;

public record RouteDecision(
        Route route,
        String strategyName,
        String explanation
) {
    public RouteDecision {
        if (route == null || strategyName == null || explanation == null) {
            throw new IllegalArgumentException("Decision fields cannot be null");
        }
    }
}