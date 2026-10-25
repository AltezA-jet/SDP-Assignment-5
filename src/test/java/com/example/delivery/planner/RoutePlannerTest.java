package com.example.delivery.planner;

import com.example.delivery.demo.DemoData;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.RouteDecision;
import com.example.delivery.strategy.CheapestRouteStrategy;
import com.example.delivery.strategy.FastestRouteStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePlannerTest {

    @Test
    void plannerUsesAssignedStrategy() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        RouteDecision decision = planner.plan(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        assertEquals("Fastest", decision.strategyName());
        assertEquals("R1", decision.route().id());
    }

    @Test
    void plannerCanSwitchStrategyAtRuntime() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        RouteDecision first = planner.plan(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        planner.setStrategy(new CheapestRouteStrategy());

        RouteDecision second = planner.plan(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.STANDARD)
        );

        assertEquals("Fastest", first.strategyName());
        assertEquals("Cheapest", second.strategyName());
        assertEquals("R1", first.route().id());
        assertEquals("R7", second.route().id());
    }

    @Test
    void decisionContainsExplanation() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        RouteDecision decision = planner.plan(
                DemoData.createRoutes(),
                DemoData.createRequest(DeliveryPriority.URGENT)
        );

        assertTrue(decision.explanation().contains("Fastest"));
        assertTrue(decision.explanation().contains("R1"));
    }

    @Test
    void constructorRejectsNullStrategy() {
        assertThrows(
                NullPointerException.class,
                () -> new RoutePlanner(null)
        );
    }

    @Test
    void setterRejectsNullStrategy() {
        RoutePlanner planner = new RoutePlanner(new FastestRouteStrategy());

        assertThrows(
                NullPointerException.class,
                () -> planner.setStrategy(null)
        );
    }
}