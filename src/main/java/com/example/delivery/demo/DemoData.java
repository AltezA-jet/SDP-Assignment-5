package com.example.delivery.demo;

import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Location;
import com.example.delivery.model.Route;

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
                new Route("R1", ORIGIN, DESTINATION, 8, 20, 12, 2.1, true, 90, 75),
                new Route("R2", ORIGIN, DESTINATION, 10, 25, 8, 1.5, true, 95, 98),
                new Route("R3", ORIGIN, DESTINATION, 12, 30, 6, 1.0, true, 85, 93),
                new Route("R4", ORIGIN, DESTINATION, 9, 22, 15, 0.9, true, 98, 99),
                new Route("R5", ORIGIN, DESTINATION, 14, 35, 5, 0.8, true, 80, 96),
                new Route("R6", ORIGIN, DESTINATION, 11, 28, 9, 1.2, true, 92, 97),
                new Route("R7", ORIGIN, DESTINATION, 16, 40, 4, 0.7, true, 75, 91),
                new Route("R8", ORIGIN, DESTINATION, 7, 18, 10, 2.8, false, 88, 70)
        );
    }

    public static DeliveryRequest createRequest(DeliveryPriority priority) {
        return new DeliveryRequest(
                ORIGIN,
                DESTINATION,
                priority,
                50.0,
                90,
                20.0,
                Set.of()
        );
    }
}