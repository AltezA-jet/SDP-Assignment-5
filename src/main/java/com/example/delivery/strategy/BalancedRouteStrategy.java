package com.example.delivery.strategy;

import com.example.delivery.model.DeliveryRequest;
import com.example.delivery.model.Route;

import java.util.Comparator;
import java.util.List;

public class BalancedRouteStrategy extends AbstractRouteStrategy {

        private final double timeWeight;
        private final double costWeight;
        private final double distanceWeight;
        private final double emissionWeight;

        public BalancedRouteStrategy() {
                this(0.35, 0.25, 0.20, 0.20);
        }

        public BalancedRouteStrategy(
                        double timeWeight,
                        double costWeight,
                        double distanceWeight,
                        double emissionWeight) {

                double sum = timeWeight + costWeight
                                + distanceWeight + emissionWeight;

                if (!Double.isFinite(timeWeight)
                                || !Double.isFinite(costWeight)
                                || !Double.isFinite(distanceWeight)
                                || !Double.isFinite(emissionWeight)
                                || timeWeight < 0
                                || costWeight < 0
                                || distanceWeight < 0
                                || emissionWeight < 0
                                || Math.abs(sum - 1.0) > 1e-9) {
                        throw new IllegalArgumentException(
                                        "Weights must be non-negative and sum to 1.0");
                }

                this.timeWeight = timeWeight;
                this.costWeight = costWeight;
                this.distanceWeight = distanceWeight;
                this.emissionWeight = emissionWeight;
        }

        @Override
        public Route selectRoute(List<Route> routes, DeliveryRequest request) {
                List<Route> eligible = getEligibleRoutes(routes, request);

                double maxTime = eligible.stream()
                                .mapToDouble(Route::estimatedMinutes).max().orElse(1);

                double maxCost = eligible.stream()
                                .mapToDouble(this::calculateTotalCost).max().orElse(1);

                double maxDistance = eligible.stream()
                                .mapToDouble(Route::distanceKm).max().orElse(1);

                double maxEmissions = eligible.stream()
                                .mapToDouble(Route::co2Kg).max().orElse(1);

                return eligible.stream()
                                .min(Comparator.comparingDouble(
                                                route -> score(route, maxTime, maxCost, maxDistance, maxEmissions)))
                                .orElseThrow();
        }

        private double score(
                        Route route,
                        double maxTime,
                        double maxCost,
                        double maxDistance,
                        double maxEmissions) {

                return timeWeight * normalize(route.estimatedMinutes(), maxTime)
                                + costWeight * normalize(calculateTotalCost(route), maxCost)
                                + distanceWeight * normalize(route.distanceKm(), maxDistance)
                                + emissionWeight * normalize(route.co2Kg(), maxEmissions);
        }

        private double normalize(double value, double maximum) {
                return maximum == 0 ? 0 : value / maximum;
        }

        @Override
        public String getStrategyName() {
                return "Balanced";
        }

        @Override
        public String explainSelection(Route route) {
        return "Lowest normalized weighted score: time 35%, cost 25%, "
                + "distance 20%, emissions 20%.";
        }
}