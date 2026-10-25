package com.example.delivery.strategy;

import com.example.delivery.demo.DemoData;
import com.example.delivery.exception.NoSuitableRouteException;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteStrategyTest {

    private final List<Route> routes = DemoData.createRoutes();
    private final DeliveryRequest request =
            DemoData.createRequest(DeliveryPriority.STANDARD);

    @Test
    void fastestStrategySelectsFastestAvailableRoute() {
        Route route = new FastestRouteStrategy()
                .selectRoute(routes, request);

        assertEquals("R1", route.id());
    }

    @Test
    void cheapestStrategySelectsLowestTotalCostRoute() {
        Route route = new CheapestRouteStrategy()
                .selectRoute(routes, request);

        assertEquals("R7", route.id());
    }

    @Test
    void ecoFriendlyStrategySelectsLowestEcoScoreRoute() {
        Route route = new EcoFriendlyRouteStrategy()
                .selectRoute(routes, request);

        assertEquals("R1", route.id());
    }

    @Test
    void balancedStrategySelectsAnAvailableRoute() {
        Route route = new BalancedRouteStrategy()
                .selectRoute(routes, request);

        assertNotNull(route);
        assertTrue(route.available());
    }

    @Test
    void safestStrategySelectsRouteWithHighestSafetyScore() {
        Route route = new SafestRouteStrategy()
                .selectRoute(routes, request);

        assertEquals("R4", route.id());
        assertEquals(98, route.safetyScore());
    }

    @Test
    void unavailableRouteIsNeverSelected() {
        for (RouteStrategy strategy : List.of(
                new FastestRouteStrategy(),
                new CheapestRouteStrategy(),
                new EcoFriendlyRouteStrategy(),
                new BalancedRouteStrategy(),
                new SafestRouteStrategy())) {

            Route route = strategy.selectRoute(routes, request);

            assertNotEquals("R8", route.id());
            assertTrue(route.available());
        }
    }

    @Test
    void prohibitedRouteIsNotSelected() {
        DeliveryRequest restricted = new DeliveryRequest(
                request.origin(),
                request.destination(),
                request.priority(),
                50.0,
                90,
                20.0,
                Set.of("R7")
        );

        Route route = new CheapestRouteStrategy()
                .selectRoute(routes, restricted);

        assertNotEquals("R7", route.id());
        assertEquals("R5", route.id());
    }

    @Test
    void emissionLimitIsRespected() {
        DeliveryRequest restricted = new DeliveryRequest(
                request.origin(),
                request.destination(),
                request.priority(),
                50.0,
                90,
                0.85,
                Set.of()
        );

        Route route = new EcoFriendlyRouteStrategy()
                .selectRoute(routes, restricted);

        assertTrue(route.co2Kg() <= 0.85);
    }

    @Test
    void deliveryDeadlineIsRespected() {
        DeliveryRequest restricted = new DeliveryRequest(
                request.origin(),
                request.destination(),
                request.priority(),
                50.0,
                21,
                20.0,
                Set.of()
        );

        Route route = new FastestRouteStrategy()
                .selectRoute(routes, restricted);

        assertTrue(route.estimatedMinutes() <= 21);
        assertEquals("R1", route.id());
    }

    @Test
    void totalCostMustRespectBudget() {
        DeliveryRequest restricted = new DeliveryRequest(
                request.origin(),
                request.destination(),
                request.priority(),
                7.0,
                90,
                20.0,
                Set.of()
        );

        Route route = new CheapestRouteStrategy()
                .selectRoute(routes, restricted);

        assertTrue(route.cost() + route.distanceKm() * 0.15 <= 7.0);
        assertEquals("R7", route.id());
    }

    @Test
    void throwsExceptionWhenNoRouteFitsBudget() {
        DeliveryRequest restricted = new DeliveryRequest(
                request.origin(),
                request.destination(),
                request.priority(),
                0.0,
                90,
                20.0,
                Set.of()
        );

        assertThrows(
                NoSuitableRouteException.class,
                () -> new CheapestRouteStrategy()
                        .selectRoute(routes, restricted)
        );
    }

    @Test
    void throwsExceptionWhenRouteListIsEmpty() {
        assertThrows(
                NoSuitableRouteException.class,
                () -> new FastestRouteStrategy()
                        .selectRoute(List.of(), request)
        );
    }

    @Test
    void balancedStrategyRejectsInvalidWeights() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(0.5, 0.5, 0.5, 0.5)
        );
    }

    @Test
    void ecoStrategyRejectsInvalidWeights() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EcoFriendlyRouteStrategy(0.8, 0.8)
        );
    }

    @Test
    void fastestStrategyHasName() {
        assertEquals("Fastest", new FastestRouteStrategy().getStrategyName());
    }
}