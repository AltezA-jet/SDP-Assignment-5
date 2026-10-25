package com.example.delivery;

import com.example.delivery.comparison.RouteComparison;
import com.example.delivery.demo.DemoData;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.planner.RoutePlanner;
import com.example.delivery.selector.StrategySelector;
import com.example.delivery.strategy.*;


import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        List<Route> routes = DemoData.createRoutes();
        StrategySelector selector = new StrategySelector();

        System.out.println("=== Smart Delivery Route Optimization ===");
        System.out.println("Available candidate routes: " + routes.size());

        RoutePlanner planner = new RoutePlanner(
                selector.selectStrategy(DeliveryPriority.STANDARD)
        );

        for (DeliveryPriority priority : DeliveryPriority.values()) {
            DeliveryRequest request = DemoData.createRequest(priority);
            planner.setStrategy(selector.selectStrategy(priority));

            try {
                RouteDecision decision = planner.plan(routes, request);

                System.out.println("\n=== Priority: " + priority + " ===");
                System.out.println("Strategy: " + decision.strategyName());
                System.out.println("Selected route: " + decision.route().id());
                System.out.println("Distance: " + decision.route().distanceKm() + " km");
                System.out.println("Time: " + decision.route().estimatedMinutes() + " min");
                System.out.println("Base cost: " + decision.route().cost());
                System.out.println("CO2 emissions: " + decision.route().co2Kg() + " kg");
                System.out.println("Reason: " + decision.explanation());

                System.out.println("Other routes:");
                decision.rejectedRouteReasons().forEach(
                        reason -> System.out.println(" - " + reason)
                );

            } catch (RuntimeException exception) {
                System.out.println("\nPriority: " + priority);
                System.out.println("Planning failed: " + exception.getMessage());
            }
        }

        System.out.println("\n=== Comparison of All Strategies ===");

        RouteComparison comparison = new RouteComparison(List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(),
                new SafestRouteStrategy(),
                new PromiseKeeperRouteStrategy()
        ));

        Map<String, String> results = comparison.compare(
                routes,
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        results.forEach((name, result) ->
                System.out.println(name + ": " + result)
        );
    }
}