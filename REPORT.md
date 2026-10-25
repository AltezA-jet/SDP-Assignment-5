# Smart Delivery & Route Optimization Engine
## Assignment 5 — Strategy Pattern

**Language:** Java 17+
**Build tool:** Maven
**Testing:** JUnit 5
**Pattern:** Strategy
**Student:** Temujin Gonchar
**Group:** SE-2518

---

## 1. Project Overview

The Smart Delivery & Route Optimization Engine selects a suitable
delivery route according to business requirements.

Different deliveries require different optimization goals. An urgent
delivery prioritizes time, an economy delivery prioritizes cost, and
an eco-friendly delivery prioritizes environmental impact.

The Strategy Pattern allows the application to switch between
different route-selection algorithms without changing the RoutePlanner
implementation.

The application uses eight demonstration routes with different
distances, estimated delivery times, costs, CO2 emissions, availability,
safety scores and historical on-time delivery rates.

All demonstration metrics are synthetic and do not represent real
logistics data.

## 2. Architecture

The implementation separates responsibilities into several components.

- **RoutePlanner:** Context that delegates route selection to the
  current strategy.
- **RouteStrategy:** Interface defining the common strategy contract.
- **AbstractRouteStrategy:** Shared filtering of routes against
  delivery constraints.
- **Concrete strategies:** Implement different route-selection goals.
- **StrategySelector:** Maps delivery priorities to strategies.
- **RouteComparison:** Executes multiple strategies for the same
  request and compares their results.
- **RouteDecision:** Contains the selected route, strategy name,
  explanation and rejected-route reasons.
- **NoSuitableRouteException:** Reports when no route satisfies the
  request constraints.

## 3. Implemented Strategies

### 3.1 FastestRouteStrategy

Selects the route with the smallest estimated delivery time.

When times are equal, the strategy prefers a higher safety score and
then a lower total cost.

### 3.2 CheapestRouteStrategy

Selects the route with the lowest calculated total cost.

The formula is:

totalCost = baseCost + distanceKm * 0.15

The distance coefficient represents an additional distance-based
operational charge used for demonstration purposes.

### 3.3 EcoFriendlyRouteStrategy

Selects the route with the lowest environmental score.

ecoScore = carbonEmission * emissionWeight
         + distanceKm * distanceWeight

The default emission weight is 0.7 and the distance weight is 0.3.
The weights can be configured and must sum to 1.0.

### 3.4 BalancedRouteStrategy

Balances four criteria using normalized values.

score = normalizedTime * timeWeight
      + normalizedCost * costWeight
      + normalizedDistance * distanceWeight
      + normalizedEmission * emissionWeight

Default weights:

- Time: 0.35
- Cost: 0.25
- Distance: 0.20
- Emissions: 0.20

Weights are configurable and must be non-negative and sum to 1.0.
The route with the lowest score is preferred.

### 3.5 SafestRouteStrategy

Selects the route with the highest safety score.
If safety scores are equal, it prefers shorter delivery time and then
lower total cost.

### 3.6 PromiseKeeperRouteStrategy

Selects the eligible route with the highest historical on-time
delivery rate.

If rates are equal, it prefers a higher safety score, shorter
delivery time and lower total cost.

This strategy represents reliability rather than raw speed.
Its on-time rates are synthetic demonstration values, not real
predictions or guarantees of future delivery performance.

## 4. Dynamic Strategy Selection

The StrategySelector maps delivery priorities as follows:

| Priority | Strategy |
|---|---|
| URGENT | FastestRouteStrategy |
| ECONOMY | CheapestRouteStrategy |
| ECO | EcoFriendlyRouteStrategy |
| STANDARD | BalancedRouteStrategy |

The mapping can be changed by registering a different strategy for
a priority.

## 5. Runtime Switching

RoutePlanner stores a RouteStrategy reference and exposes setStrategy()
to replace the current algorithm while the application is running.

The planner delegates selection through the interface and does not
need to implement each concrete algorithm itself.

This demonstrates interchangeable algorithms through polymorphism.

## 6. Hard Constraints

Before ranking candidates, the shared route filtering checks:

1. Route availability.
2. Matching origin and destination.
3. Prohibited route identifiers.
4. Maximum total cost.
5. Maximum delivery time.
6. Maximum allowed CO2 emissions.

An ineligible route cannot win merely because its optimization score
is otherwise favorable.

If no route passes the constraints, the application throws
NoSuitableRouteException rather than returning null.

## 7. Route Explanation and Comparison

RoutePlanner produces a RouteDecision with the selected route,
strategy name, selection explanation and reasons why other routes
were rejected or not selected.

RouteComparison executes each configured strategy against the same
request and returns the selected route details for comparison.

The explanations describe the relevant strategy criteria and route
metrics.

## 8. Error Handling and Edge Cases

The implementation handles several invalid conditions, including:

- An empty candidate-route list.
- No route satisfying all delivery constraints.
- Invalid weights in the Balanced strategy.
- Invalid weights in the EcoFriendly strategy.
- Invalid route metrics, such as negative cost or distance.
- Invalid coordinates.
- Null strategy registration.
- Invalid on-time delivery rates.

JUnit tests cover these cases alongside normal strategy selection.

## 9. Design Patterns and SOLID

### Strategy Pattern

The pattern encapsulates route-selection algorithms behind the
RouteStrategy interface and allows them to be replaced independently.

### Single Responsibility Principle

RoutePlanner coordinates selection, concrete strategies optimize
routes, StrategySelector chooses algorithms, and RouteComparison
compares results.

### Open/Closed Principle

A new strategy can be added as another implementation without
rewriting RoutePlanner.

### Dependency Inversion Principle

RoutePlanner depends on the RouteStrategy abstraction rather than
on a specific concrete strategy.

## 10. Strategy Compared with Bridge

Strategy encapsulates interchangeable algorithms that solve the
same problem in different ways.

Bridge separates an abstraction from its implementation hierarchy
so both can evolve independently.

In this project, the route planner uses Strategy because the main
requirement is to choose and switch between optimization algorithms.

## 11. UML Diagrams

The project includes:

- `docs/CLASS_DIAGRAM.puml`
- `docs/SEQUENCE_DIAGRAM.puml`

The class diagram illustrates the relationship between RoutePlanner,
RouteStrategy, concrete strategies, StrategySelector and domain models.

The sequence diagram illustrates the route-selection interaction.

## 12. Testing

Tests are implemented using JUnit 5.

They cover strategy selection, runtime switching, automatic priority
mapping, route constraints, comparison mode, invalid weights,
boundary conditions and failure scenarios.

**Test execution result:** Record the actual result after running
`mvn clean test`. Do not report a successful build until Maven
confirms that all tests pass.

## 13. Limitations

The application currently uses synthetic routes and metrics.
It does not retrieve live traffic, weather, road closures, real
delivery prices, or real historical delivery statistics.

The PromiseKeeper strategy uses a historical on-time rate supplied
with each route. A production implementation would need validated
historical data and potentially a separate prediction model.

The cost formula and strategy weights are illustrative and should be
calibrated using real business requirements before production use.

## 14. Conclusion

The project demonstrates how the Strategy Pattern can separate
route-selection algorithms from the route-planning context.

The application supports multiple optimization goals, runtime
switching, automatic strategy selection, hard constraints,
route explanations and comparison mode.

The design permits new strategies to be added with minimal changes
to the existing planning architecture.