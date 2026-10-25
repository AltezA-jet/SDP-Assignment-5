package com.example.delivery.model;

import java.util.List;

public record RouteDecision(
        Route route,
        String strategyName,
        String explanation,
        List<String> rejectedRouteReasons
) {
    public RouteDecision {
        if (route == null || strategyName == null || explanation == null
                || rejectedRouteReasons == null) {
            throw new IllegalArgumentException("Decision fields cannot be null");
        }

        rejectedRouteReasons = List.copyOf(rejectedRouteReasons);
    }
}