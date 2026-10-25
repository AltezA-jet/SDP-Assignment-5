# Smart Delivery & Route Optimization Engine

A Java 17+ Maven project demonstrating the Strategy Design Pattern
through a delivery route optimization system.

## Features

- Five interchangeable route selection strategies.
- Runtime strategy switching.
- Automatic strategy selection based on delivery priority.
- Budget, deadline, availability and emissions constraints.
- Prohibited route handling.
- Comparison of multiple strategies.
- Explanations for selected and rejected routes.
- JUnit 5 unit tests.

## Technologies

- Java 17+
- Maven
- JUnit 5
- Strategy Design Pattern

## Project Structure

- `model` — locations, routes, requests and decisions.
- `strategy` — route selection algorithms.
- `planner` — execution context for a selected strategy.
- `selector` — maps priorities to strategies.
- `comparison` — compares the results of strategies.
- `exception` — errors for missing suitable routes.
- `demo` — sample delivery data.
- `docs` — UML diagrams and project documentation.

## Strategies

### 1. Fastest

Selects the route with the lowest estimated delivery time.
Safety score and total cost are used as tie-breakers.

### 2. Cheapest

Minimizes total cost:

`totalCost = baseCost + distanceKm * 0.15`

The distance coefficient represents an additional distance-based
operational charge.

### 3. EcoFriendly

Minimizes an environmental score:

`ecoScore = CO2 * 0.7 + distanceKm * 0.3`

The weights are configurable and must sum to 1.0.

### 4. Balanced

Normalizes time, cost, distance and emissions, then calculates
a weighted score:

`score = 0.35*time + 0.25*cost + 0.20*distance + 0.20*emissions`

Each metric is divided by its maximum value among eligible routes.
The route with the lowest score is selected.

### 5. Safest

Selects the route with the highest safety score.
Estimated time and total cost break ties.

## Delivery Priorities

| Priority | Strategy |
|---|---|
| URGENT | Fastest |
| ECONOMY | Cheapest |
| ECO | EcoFriendly |
| STANDARD | Balanced |

The selector supports registering a different strategy for a priority.

## Constraints

A route is eligible only when:

- It is available.
- Its origin and destination match the request.
- It is not prohibited.
- Its total cost does not exceed the budget.
- Its estimated delivery time meets the deadline.
- Its emissions do not exceed the allowed limit.

If no route satisfies the constraints, the system throws
`NoSuitableRouteException` instead of returning `null`.

## Run and Test

Run all unit tests:

```bash
mvn clean test