package com.example.delivery.transport;

import com.example.delivery.demo.DemoData;
import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Location;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;
import com.example.delivery.model.TrafficLevel;
import com.example.delivery.planner.RoutePlanner;
import com.example.delivery.strategy.FastestRouteStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportModeTest {

    private final Location origin =
            new Location("Warehouse", 51.1694, 71.4491);

    private final Location destination =
            new Location("Customer", 51.1282, 71.4304);

    private Route createRoute(
            String id,
            double distance,
            RouteType type,
            TrafficLevel traffic) {

        return new Route(
                id,
                origin,
                destination,
                distance,
                60,
                10,
                2,
                true,
                90,
                95,
                type,
                traffic
        );
    }

    @Test
    void pedestrianCannotUseHighway() {
        Route highway = createRoute(
                "H1", 10, RouteType.HIGHWAY, TrafficLevel.LOW
        );

        PedestrianCourierMode mode = new PedestrianCourierMode();

        assertFalse(mode.supports(highway));
        assertFalse(mode.adaptRoute(highway).available());
    }

    @Test
    void carCannotUseParkPath() {
        Route park = createRoute(
                "P1", 5, RouteType.PARK_PATH, TrafficLevel.LOW
        );

        CarCourierMode mode = new CarCourierMode();

        assertFalse(mode.supports(park));
        assertFalse(mode.adaptRoute(park).available());
    }

    @Test
    void bicycleLaneIsFasterThanCityStreetForBicycle() {
        Route bikeLane = createRoute(
                "B1", 6, RouteType.BIKE_LANE, TrafficLevel.LOW
        );

        Route cityStreet = createRoute(
                "C1", 6, RouteType.CITY_STREET, TrafficLevel.LOW
        );

        BicycleCourierMode mode = new BicycleCourierMode();

        assertTrue(
                mode.adaptRoute(bikeLane).estimatedMinutes()
                        < mode.adaptRoute(cityStreet).estimatedMinutes()
        );
    }

    @Test
    void highTrafficAffectsCarMoreThanPedestrian() {
        Route lowTraffic = createRoute(
                "C1", 8, RouteType.CITY_STREET, TrafficLevel.LOW
        );

        Route highTraffic = createRoute(
                "C2", 8, RouteType.CITY_STREET, TrafficLevel.HIGH
        );

        CarCourierMode car = new CarCourierMode();
        PedestrianCourierMode pedestrian = new PedestrianCourierMode();

        int carDifference =
                car.adaptRoute(highTraffic).estimatedMinutes()
                        - car.adaptRoute(lowTraffic).estimatedMinutes();

        int pedestrianDifference =
                pedestrian.adaptRoute(highTraffic).estimatedMinutes()
                        - pedestrian.adaptRoute(lowTraffic).estimatedMinutes();

        assertTrue(carDifference > pedestrianDifference);
    }

    @Test
    void carProducesMoreCo2ThanBicycleForSameRoute() {
        Route route = createRoute(
                "C1", 10, RouteType.CITY_STREET, TrafficLevel.LOW
        );

        double carCo2 = new CarCourierMode().adaptRoute(route).co2Kg();
        double bicycleCo2 =
                new BicycleCourierMode().adaptRoute(route).co2Kg();

        assertTrue(carCo2 > bicycleCo2);
    }

    @Test
    void plannerSelectsBikeLaneForFastBicycleDelivery() {
        DeliveryRequest request =
                DemoData.createRequest(DeliveryPriority.URGENT);

        RoutePlanner planner = new RoutePlanner(
                new FastestRouteStrategy(),
                new BicycleCourierMode()
        );

        Route selected = planner.plan(
                DemoData.createRoutes(),
                request
        ).route();

        assertEquals("R10", selected.id());
        assertEquals(RouteType.BIKE_LANE, selected.routeType());
    }

    @Test
    void pedestrianPlannerCanSelectParkShortcut() {
        DeliveryRequest request =
                DemoData.createRequest(DeliveryPriority.URGENT);

        RoutePlanner planner = new RoutePlanner(
                new FastestRouteStrategy(),
                new PedestrianCourierMode()
        );

        Route selected = planner.plan(
                DemoData.createRoutes(),
                request
        ).route();

        assertEquals("R9", selected.id());
        assertEquals(RouteType.PARK_PATH, selected.routeType());
    }
}