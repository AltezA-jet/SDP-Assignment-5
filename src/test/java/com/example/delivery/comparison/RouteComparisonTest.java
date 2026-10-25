package com.example.delivery.comparison;

import com.example.delivery.demo.DemoData;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.strategy.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteComparisonTest {

    private final List<RouteStrategy> strategies = List.of(
            new FastestRouteStrategy(),
            new CheapestRouteStrategy(),
            new EcoFriendlyRouteStrategy(),
            new BalancedRouteStrategy(),
            new SafestRouteStrategy()
    );

    @Test
    void comparisonRunsEveryStrategy() {
        RouteComparison comparison = new RouteComparison(strategies);

        Map<String, String> results = comparison.compare(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        assertEquals(5, results.size());
        assertTrue(results.containsKey("Fastest"));
        assertTrue(results.containsKey("Cheapest"));
        assertTrue(results.containsKey("EcoFriendly"));
        assertTrue(results.containsKey("Balanced"));
        assertTrue(results.containsKey("Safest"));
    }

    @Test
    void comparisonContainsSelectedRouteDetails() {
        RouteComparison comparison = new RouteComparison(strategies);

        Map<String, String> results = comparison.compare(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        assertTrue(results.get("Fastest").contains("R1"));
        assertTrue(results.get("Cheapest").contains("R7"));
    }

    @Test
    void comparisonReportsWhenNoRouteIsSuitable() {
        RouteComparison comparison = new RouteComparison(strategies);

        DeliveryRequest restrictiveRequest = new DeliveryRequest(
                DemoData.createRequest(DeliveryPriority.STANDARD).origin(),
                DemoData.createRequest(DeliveryPriority.STANDARD).destination(),
                DeliveryPriority.STANDARD,
                0.0,
                90,
                20.0,
                Set.of()
        );

        Map<String, String> results = comparison.compare(
                DemoData.createRoutes(),
                restrictiveRequest
        );

        assertEquals(5, results.size());
        assertTrue(results.values().stream()
                .allMatch(result -> result.contains("No suitable route")));
    }

    @Test
    void constructorRejectsEmptyStrategyList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RouteComparison(List.of())
        );
    }

    @Test
    void comparisonRejectsNullStrategyList() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RouteComparison(null)
        );
    }
}