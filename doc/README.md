# Buratino — User Documentation

Buratino is a trading robot. You define high-level trading strategies called **assignments** for securities through a
REST API, and the application autonomously manages the underlying broker orders to fulfil each strategy. You don't place
individual orders by hand: you tell Buratino what you want to achieve (for example, "sell one lot at the best price in
the book, but only if a 0.1% spread is guaranteed"), and Buratino takes care of placing, updating and cancelling broker
orders for you.

## Capabilities

- **Multiple trading strategies** for shares and futures:
    - [Top Price](assignments/top-price.md) — sell/buy 1 lot at the best price in the order book.
    - [Fractional Spread](assignments/fractional-spread.md) — sell/buy 1 lot at the best price that still guarantees a
      configured spread vs. the opposite side of the order book.
    - [Repeatable Fractional Spread](assignments/repeatable-fractional-spread.md) — endless cycle of Fractional Spread
      assignments that flips direction on every fill to accumulate spread profit.
- **Auto-refresh** — schedule periodic reconciliation of an assignment with the current market state.
  See [Auto-refresh](auto-refresh.md).
- **REST API** — every action (create, list, fetch, refresh, refresh-all, cancel) is exposed over HTTP and documented in
  an interactive Swagger UI.
- **Manual control** — at any moment you can manually refresh a single assignment, refresh all in-progress assignments
  of a given strategy, or cancel an assignment.
- **Read-only inspection** of instruments and locally tracked orders.

## Table of contents

| Document                                                                             | Purpose                                                           |
|--------------------------------------------------------------------------------------|-------------------------------------------------------------------|
| [Instruments](instruments.md)                                                        | What an instrument is and how to look one up.                     |
| [Orders](orders.md)                                                                  | What an order is from your perspective and how to inspect orders. |
| [Assignments — overview](assignments/overview.md)                                    | Assignment concept, lifecycle, common operations, glossary.       |
| [Top Price strategy](assignments/top-price.md)                                       | Strategy details, parameters, endpoints, examples.                |
| [Fractional Spread strategy](assignments/fractional-spread.md)                       | Strategy details, pricing rule, endpoints, examples.              |
| [Repeatable Fractional Spread strategy](assignments/repeatable-fractional-spread.md) | Cycling strategy, endpoints, examples.                            |
| [Auto-refresh](auto-refresh.md)                                                      | How periodic reconciliation works.                                |
| [API reference](api-reference.md)                                                    | Consolidated endpoint table and response codes.                   |
