package com.example.delivery.demo;

import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Location;
import com.example.delivery.model.Route;
import com.example.delivery.model.RouteType;
import com.example.delivery.model.TrafficLevel;

import java.util.List;
import java.util.Set;

public final class DemoData {

    private static final Location ORIGIN =
            new Location("Central Warehouse", 51.1694, 71.4491);

    private static final Location DESTINATION =
            new Location("Delivery Center", 51.1282, 71.4304);

    private DemoData() {
    }

    public static List<Route> createRoutes() {
        return List.of(
                new Route("R1", ORIGIN, DESTINATION,
                        8, 20, 12, 2.1, true, 90, 75,
                        RouteType.CITY_STREET, TrafficLevel.MEDIUM),

                new Route("R2", ORIGIN, DESTINATION,
                        10, 25, 8, 1.5, true, 95, 98,
                        RouteType.CITY_STREET, TrafficLevel.HIGH),

                new Route("R3", ORIGIN, DESTINATION,
                        12, 30, 6, 1.0, true, 85, 93,
                        RouteType.MIXED_ROAD, TrafficLevel.MEDIUM),

                new Route("R4", ORIGIN, DESTINATION,
                        9, 22, 15, 0.9, true, 98, 99,
                        RouteType.CITY_STREET, TrafficLevel.LOW),

                new Route("R5", ORIGIN, DESTINATION,
                        14, 35, 5, 0.8, true, 80, 96,
                        RouteType.HIGHWAY, TrafficLevel.LOW),

                new Route("R6", ORIGIN, DESTINATION,
                        11, 28, 9, 1.2, true, 92, 97,
                        RouteType.MIXED_ROAD, TrafficLevel.HIGH),

                new Route("R7", ORIGIN, DESTINATION,
                        16, 40, 4, 0.7, true, 75, 91,
                        RouteType.HIGHWAY, TrafficLevel.LOW),

                new Route("R8", ORIGIN, DESTINATION,
                        7, 18, 10, 2.8, false, 88, 70,
                        RouteType.PARK_PATH, TrafficLevel.LOW),

                new Route("R9", ORIGIN, DESTINATION,
                        5, 55, 22, 3.0, true, 88, 82,
                        RouteType.PARK_PATH, TrafficLevel.LOW),

                new Route("R10", ORIGIN, DESTINATION,
                        4, 40, 22, 3.3, true, 91, 86,
                        RouteType.BIKE_LANE, TrafficLevel.LOW),

                new Route("R11", ORIGIN, DESTINATION,
                        22, 60, 28, 5.0, true, 94, 90,
                        RouteType.HIGHWAY, TrafficLevel.MEDIUM),

                new Route("R12", ORIGIN, DESTINATION,
                        13, 45, 23, 4.0, true, 87, 88,
                        RouteType.MIXED_ROAD, TrafficLevel.LOW),

                new Route("R13", ORIGIN, DESTINATION,
                        6, 42, 18, 4.2, true, 92, 89,
                        RouteType.BIKE_LANE, TrafficLevel.LOW),

                new Route("R14", ORIGIN, DESTINATION,
                        6.5, 50, 19, 3.8, true, 89, 87,
                        RouteType.CITY_STREET, TrafficLevel.HIGH)
        );
    }

    public static DeliveryRequest createRequest(DeliveryPriority priority) {
        return new DeliveryRequest(
                ORIGIN,
                DESTINATION,
                priority,
                50.0,
                180,
                20.0,
                Set.of()
        );
    }
}