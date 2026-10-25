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
import java.util.Locale;

public class Main {

    private static final String LINE = "=".repeat(64);
    private static final String SEPARATOR = "-".repeat(64);

    public static void main(String[] args) {
        List<Route> routes = DemoData.createRoutes();
        StrategySelector selector = new StrategySelector();

        System.out.println(LINE);
        System.out.println("       SMART DELIVERY & ROUTE OPTIMIZATION");
        System.out.println(LINE);
        System.out.println("Candidate routes: " + routes.size());

        RoutePlanner planner = new RoutePlanner(
                selector.selectStrategy(DeliveryPriority.STANDARD)
        );

        for (DeliveryPriority priority : DeliveryPriority.values()) {
            DeliveryRequest request = DemoData.createRequest(priority);
            planner.setStrategy(selector.selectStrategy(priority));

            System.out.println("\n" + LINE);
            System.out.println("DELIVERY PRIORITY: " + priority);
            System.out.println(SEPARATOR);

            try {
                RouteDecision decision = planner.plan(routes, request);
                Route route = decision.route();

                System.out.println("Strategy:       " + decision.strategyName());
                System.out.println("Selected route: " + route.id());

                System.out.println("\nROUTE DETAILS");
                System.out.println("  Distance:       " + route.distanceKm() + " km");
                System.out.println("  Delivery time:  " + route.estimatedMinutes() + " min");
                System.out.println("  Base cost:      " + format(route.cost()));
                System.out.println("  Total cost:     " + format(totalCost(route)));
                System.out.println("  CO2 emissions:  " + route.co2Kg() + " kg");
                System.out.println("  Safety score:   " + route.safetyScore() + "/100");
                System.out.println("  On-time rate:   " + route.onTimeRate() + "%");

                System.out.println("\nWHY THIS ROUTE?");
                System.out.println("  " + decision.explanation());

                System.out.println("\nOTHER ROUTES");
                if (decision.rejectedRouteReasons().isEmpty()) {
                    System.out.println("  No other candidate routes.");
                } else {
                    decision.rejectedRouteReasons().forEach(
                            reason -> System.out.println("  - " + reason)
                    );
                }

            } catch (RuntimeException exception) {
                System.out.println("Planning failed: " + exception.getMessage());
            }
        }

        printComparison(routes);
    }

    private static void printComparison(List<Route> routes) {
        DeliveryRequest request =
                DemoData.createRequest(DeliveryPriority.STANDARD);

        List<RouteStrategy> strategies = List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(),
                new SafestRouteStrategy(),
                new PromiseKeeperRouteStrategy()
        );

        RouteComparison comparison = new RouteComparison(strategies);
        Map<String, String> results = comparison.compare(routes, request);

        System.out.println("\n\n" + LINE);
        System.out.println("             STRATEGY COMPARISON");
        System.out.println(LINE);
        System.out.println("Same delivery request is evaluated by every strategy.");
        System.out.println("Priority: " + request.priority());

        results.forEach((name, result) -> {
            System.out.println("\n" + SEPARATOR);
            System.out.println("STRATEGY: " + name.toUpperCase(Locale.ROOT));
            System.out.println(SEPARATOR);

            for (String detail : result.split("\\R")) {
                System.out.println("  " + detail);
            }
        });

        System.out.println("\n" + LINE);
        System.out.println("END OF COMPARISON");
        System.out.println(LINE);
    }

    private static double totalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }
}