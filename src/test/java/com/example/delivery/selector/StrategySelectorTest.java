package com.example.delivery.selector;

import com.example.delivery.model.DeliveryPriority;
import com.example.delivery.strategy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrategySelectorTest {

    private final StrategySelector selector = new StrategySelector();

    @Test
    void urgentPrioritySelectsFastestStrategy() {
        assertEquals(
                "Fastest",
                selector.selectStrategy(DeliveryPriority.URGENT).getStrategyName()
        );
    }

    @Test
    void economyPrioritySelectsCheapestStrategy() {
        assertEquals(
                "Cheapest",
                selector.selectStrategy(DeliveryPriority.ECONOMY).getStrategyName()
        );
    }

    @Test
    void ecoPrioritySelectsEcoFriendlyStrategy() {
        assertEquals(
                "EcoFriendly",
                selector.selectStrategy(DeliveryPriority.ECO).getStrategyName()
        );
    }

    @Test
    void standardPrioritySelectsBalancedStrategy() {
        assertEquals(
                "Balanced",
                selector.selectStrategy(DeliveryPriority.STANDARD).getStrategyName()
        );
    }

    @Test
    void customStrategyCanBeRegistered() {
        selector.registerStrategy(
                DeliveryPriority.STANDARD,
                new SafestRouteStrategy()
        );

        assertEquals(
                "Safest",
                selector.selectStrategy(DeliveryPriority.STANDARD).getStrategyName()
        );
    }

    @Test
    void registrationRejectsNullPriority() {
        assertThrows(
                NullPointerException.class,
                () -> selector.registerStrategy(null, new FastestRouteStrategy())
        );
    }

    @Test
    void registrationRejectsNullStrategy() {
        assertThrows(
                NullPointerException.class,
                () -> selector.registerStrategy(DeliveryPriority.URGENT, null)
        );
    }

    @Test
    void selectorRejectsNullPriority() {
        assertThrows(
                NullPointerException.class,
                () -> selector.selectStrategy(null)
        );
    }
}