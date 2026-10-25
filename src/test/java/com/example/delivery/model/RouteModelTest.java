package com.example.delivery.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteModelTest {

    private final Location origin =
            new Location("Warehouse", 51.1694, 71.4491);

    private final Location destination =
            new Location("Customer", 51.1282, 71.4304);

    private Route createRoute(
            double distance,
            double cost,
            double onTimeRate) {

        return new Route(
                "R1",
                origin,
                destination,
                distance,
                20,
                cost,
                1.5,
                true,
                90,
                onTimeRate
        );
    }

    @Test
    void acceptsMinimumOnTimeRate() {
        Route route = createRoute(5, 10, 0);

        assertEquals(0, route.onTimeRate());
    }

    @Test
    void acceptsMaximumOnTimeRate() {
        Route route = createRoute(5, 10, 100);

        assertEquals(100, route.onTimeRate());
    }

    @Test
    void rejectsOnTimeRateAbove100() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoute(5, 10, 100.1)
        );
    }

    @Test
    void rejectsNegativeOnTimeRate() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoute(5, 10, -1)
        );
    }

    @Test
    void rejectsZeroDistance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoute(0, 10, 95)
        );
    }

    @Test
    void rejectsNegativeDistance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoute(-5, 10, 95)
        );
    }

    @Test
    void rejectsNegativeCost() {
        assertThrows(
                IllegalArgumentException.class,
                () -> createRoute(5, -1, 95)
        );
    }

    @Test
    void rejectsInvalidLatitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Location("Invalid", 100, 71)
        );
    }

    @Test
    void rejectsInvalidLongitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Location("Invalid", 51, 200)
        );
    }

    @Test
    void rejectsNegativeBudget() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DeliveryRequest(
                        origin,
                        destination,
                        DeliveryPriority.STANDARD,
                        -1,
                        60,
                        10,
                        Set.of()
                )
        );
    }

    @Test
    void rejectsNegativeEmissionLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DeliveryRequest(
                        origin,
                        destination,
                        DeliveryPriority.ECO,
                        100,
                        60,
                        -1,
                        Set.of()
                )
        );
    }

    @Test
    void requestCopiesProhibitedRouteIds() {
        Set<String> prohibited = new java.util.HashSet<>();
        prohibited.add("R2");

        DeliveryRequest request = new DeliveryRequest(
                origin,
                destination,
                DeliveryPriority.STANDARD,
                100,
                60,
                10,
                prohibited
        );

        prohibited.add("R3");

        assertTrue(request.prohibitedRouteIds().contains("R2"));
        assertFalse(request.prohibitedRouteIds().contains("R3"));
    }
}