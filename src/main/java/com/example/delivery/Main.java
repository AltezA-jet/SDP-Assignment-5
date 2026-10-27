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
import com.example.delivery.transport.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String LINE = "=".repeat(64);
    private static final String SEPARATOR = "-".repeat(64);

    public static void main(String[] args) {
        List<Route> routes = DemoData.createRoutes();
        StrategySelector selector = new StrategySelector();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(LINE);
            System.out.println("       SMART DELIVERY & ROUTE OPTIMIZATION");
            System.out.println(LINE);

            System.out.println("\nChoose transport:");
            System.out.println("1. Pedestrian courier");
            System.out.println("2. Bicycle courier");
            System.out.println("3. Motorcycle courier");
            System.out.println("4. Car courier");
            System.out.println("5. Compare all transport modes");
            System.out.println("0. Exit");

            int choice = readChoice(scanner, "Your choice: ", 0, 5);

            if (choice == 0) {
                System.out.println("Goodbye.");
                return;
            }

            if (choice == 5) {
                compareTransportModes(routes);
                return;
            }

            TransportMode mode = selectTransportMode(choice);

            System.out.println("\nChoose delivery priority:");
            System.out.println("1. URGENT");
            System.out.println("2. ECONOMY");
            System.out.println("3. ECO");
            System.out.println("4. STANDARD");

            int priorityChoice = readChoice(scanner, "Your choice: ", 1, 4);

            DeliveryPriority priority = switch (priorityChoice) {
                case 1 -> DeliveryPriority.URGENT;
                case 2 -> DeliveryPriority.ECONOMY;
                case 3 -> DeliveryPriority.ECO;
                default -> DeliveryPriority.STANDARD;
            };

            DeliveryRequest request = DemoData.createRequest(priority);

            RoutePlanner planner = new RoutePlanner(
                    selector.selectStrategy(priority),
                    mode
            );

            System.out.println("\n" + LINE);
            System.out.println("DELIVERY PLAN");
            System.out.println(LINE);
            System.out.println("Transport: " + mode.getName());
            System.out.println("Priority:  " + priority);
            System.out.println("Candidates: " + routes.size());

            try {
                RouteDecision decision = planner.plan(routes, request);
                printDecision(decision);

            } catch (RuntimeException exception) {
                System.out.println("Planning failed: " + exception.getMessage());
                return;
            }

            System.out.println("\n\n" + LINE);
            System.out.println("COMPARISON OF ROUTE STRATEGIES");
            System.out.println("Transport: " + mode.getName());
            System.out.println("Priority: " + priority);
            System.out.println(LINE);

            RouteComparison comparison =
                    new RouteComparison(createStrategies());

            Map<String, String> results = comparison.compare(
                    mode.adaptRoutes(routes),
                    request
            );

            results.forEach((name, result) -> {
                System.out.println("\n" + SEPARATOR);
                System.out.println(name.toUpperCase(Locale.ROOT));
                System.out.println(SEPARATOR);

                for (String detail : result.split("\\R")) {
                    System.out.println("  " + detail);
                }
            });

            System.out.println("\n" + LINE);
            System.out.println("END OF REPORT");
            System.out.println(LINE);
        }
    }

    private static TransportMode selectTransportMode(int choice) {
        return switch (choice) {
            case 1 -> new PedestrianCourierMode();
            case 2 -> new BicycleCourierMode();
            case 3 -> new MotorcycleCourierMode();
            case 4 -> new CarCourierMode();
            default -> throw new IllegalArgumentException(
                    "Unknown transport selection"
            );
        };
    }

    private static List<TransportMode> createTransportModes() {
        return List.of(
                new PedestrianCourierMode(),
                new BicycleCourierMode(),
                new MotorcycleCourierMode(),
                new CarCourierMode()
        );
    }

    private static List<RouteStrategy> createStrategies() {
        return List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(),
                new SafestRouteStrategy(),
                new PromiseKeeperRouteStrategy()
        );
    }

    private static void compareTransportModes(List<Route> routes) {
        DeliveryRequest request =
                DemoData.createRequest(DeliveryPriority.STANDARD);

        System.out.println("\n" + LINE);
        System.out.println("TRANSPORT MODE COMPARISON");
        System.out.println("Priority: STANDARD");
        System.out.println("Route strategy: Balanced");
        System.out.println(LINE);

        for (TransportMode mode : createTransportModes()) {
            System.out.println("\n" + SEPARATOR);
            System.out.println(mode.getName().toUpperCase(Locale.ROOT));
            System.out.println(SEPARATOR);

            long compatibleRoutes = routes.stream()
                    .filter(route -> mode.supports(route))
                    .filter(Route::available)
                    .count();

            System.out.println("Compatible available routes: "
                    + compatibleRoutes);

            try {
                RoutePlanner planner = new RoutePlanner(
                        new BalancedRouteStrategy(),
                        mode
                );

                RouteDecision decision = planner.plan(routes, request);
                Route route = decision.route();

                System.out.println("Selected route: " + route.id());
                System.out.println("Distance: " + route.distanceKm() + " km");
                System.out.println("Estimated time: "
                        + route.estimatedMinutes() + " min");
                System.out.println("Total cost: "
                        + format(totalCost(route)));
                System.out.println("CO2 emissions: "
                        + format(route.co2Kg()) + " kg");
                System.out.println("Safety: "
                        + route.safetyScore() + "/100");

            } catch (RuntimeException exception) {
                System.out.println("No suitable route: "
                        + exception.getMessage());
            }
        }

        System.out.println("\n" + LINE);
        System.out.println("END OF TRANSPORT COMPARISON");
        System.out.println(LINE);
    }

    private static void printDecision(RouteDecision decision) {
        Route route = decision.route();

        System.out.println("\nSelected route: " + route.id());
        System.out.println("Route type: " + route.routeType());
        System.out.println("Traffic: " + route.trafficLevel());

        System.out.println("\nROUTE DETAILS");
        System.out.println("  Distance: " + route.distanceKm() + " km");
        System.out.println("  Estimated time: "
                + route.estimatedMinutes() + " min");
        System.out.println("  Transport-adjusted cost: "
                + format(route.cost()));
        System.out.println("  Total calculated cost: "
                + format(totalCost(route)));
        System.out.println("  CO2 emissions: "
                + format(route.co2Kg()) + " kg");
        System.out.println("  Safety: " + route.safetyScore() + "/100");
        System.out.println("  Historical on-time rate: "
                + format(route.onTimeRate()) + "%");

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
    }

    private static double totalCost(Route route) {
        return route.cost() + route.distanceKm() * 0.15;
    }

    private static String format(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static int readChoice(
            Scanner scanner,
            String prompt,
            int min,
            int max) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Show the same validation message below.
            }

            System.out.println(
                    "Enter a number from " + min + " to " + max + "."
            );
        }
    }
}