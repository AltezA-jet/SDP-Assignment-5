package com.example.delivery.selector;

import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.strategy.BalancedRouteStrategy;
import com.example.delivery.strategy.CheapestRouteStrategy;
import com.example.delivery.strategy.EcoFriendlyRouteStrategy;
import com.example.delivery.strategy.FastestRouteStrategy;
import com.example.delivery.strategy.RouteStrategy;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector() {
        registerStrategy(DeliveryPriority.URGENT, new FastestRouteStrategy());
        registerStrategy(DeliveryPriority.ECONOMY, new CheapestRouteStrategy());
        registerStrategy(DeliveryPriority.ECO, new EcoFriendlyRouteStrategy());
        registerStrategy(DeliveryPriority.STANDARD, new BalancedRouteStrategy());
    }

    public void registerStrategy(
            DeliveryPriority priority,
            RouteStrategy strategy) {

        strategies.put(
                Objects.requireNonNull(priority),
                Objects.requireNonNull(strategy)
        );
    }

    public RouteStrategy selectStrategy(DeliveryPriority priority) {
        RouteStrategy strategy = strategies.get(
                Objects.requireNonNull(priority)
        );

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy registered for priority: " + priority
            );
        }

        return strategy;
    }
}