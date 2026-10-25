package com.example.delivery;

import com.example.delivery.comparison.RouteComparison;
import com.example.delivery.demo.DemoData;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.planner.RoutePlanner;
import com.example.delivery.selector.StrategySelector;
import com.example.delivery.strategy.BalancedRouteStrategy;
import com.example.delivery.strategy.CheapestRouteStrategy;
import com.example.delivery.strategy.EcoFriendlyRouteStrategy;
import com.example.delivery.strategy.FastestRouteStrategy;
import com.example.delivery.strategy.RouteStrategy;
import com.example.delivery.strategy.SafestRouteStrategy;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        List<Route> routes = DemoData.createRoutes();
        StrategySelector selector = new StrategySelector();

        System.out.println("=== Smart Delivery Route Optimization ===");
        System.out.println("Available routes: " + routes.size());

        RoutePlanner planner = new RoutePlanner(
                selector.selectStrategy(DeliveryPriority.STANDARD)
        );

        DeliveryPriority[] priorities = {
                DeliveryPriority.STANDARD,
                DeliveryPriority.URGENT,
                DeliveryPriority.ECONOMY,
                DeliveryPriority.ECO
        };

        for (DeliveryPriority priority : priorities) {
            DeliveryRequest request = DemoData.createRequest(priority);

            planner.setStrategy(selector.selectStrategy(priority));

            RouteDecision decision = planner.plan(routes, request);

            System.out.println("\nPriority: " + priority);
            System.out.println("Strategy: " + decision.strategyName());
            System.out.println("Route: " + decision.route().id());
            System.out.println("Time: " + decision.route().estimatedMinutes() + " min");
            System.out.println("Cost: " + decision.route().cost());
            System.out.println("CO2: " + decision.route().co2Kg() + " kg");
            System.out.println("Reason: " + decision.explanation());
        }

        System.out.println("\n=== Strategy Comparison ===");

        List<RouteStrategy> strategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(),
                new SafestRouteStrategy()
        );

        RouteComparison comparison = new RouteComparison(strategies);

        DeliveryRequest request =
                DemoData.createRequest(DeliveryPriority.STANDARD);

        Map<String, String> results = comparison.compare(routes, request);

        results.forEach((strategy, result) ->
                System.out.println(strategy + ": " + result)
        );
    }
}