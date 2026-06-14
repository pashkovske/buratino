# Fractional Spread strategy

> **Goal:** sell or buy **1 lot** at the best price on your side of the order book, **but only at a price that guarantees a configured spread** vs. the opposite side of the book.

This strategy is a spread-protected variant of [Top Price](top-price.md). Instead of blindly matching the best price on your side, it compares it with the opposite side and refuses to quote more aggressively than the spread rule allows.

See [Assignment overview](overview.md) for shared concepts (lifecycle, common operations, response shape).

## The spread rule

Let `rate ∈ (0, 1)` be your configured spread (for example `0.001` = 0.1%). Let:

- `bestOtherBuy` = highest BUY price in the order book, excluding your own.
- `bestOtherSell` = lowest SELL price in the order book, excluding your own.
- `bestSell` = lowest SELL price in the book.
- `bestBuy` = highest BUY price in the book.

The strategy quotes:

- **BUY:** `price ≤ min(bestOtherBuy, bestSell × (1 − rate))`
- **SELL:** `price ≥ max(bestOtherSell, bestBuy × (1 + rate))`

In plain words:

- When **buying**, your bid is the lower of:
  - the best other bid in the book (so you stay at the top of the BUY queue), and
  - the best ask discounted by `rate` (so that if your bid is hit, the spread vs. the current ask is at least `rate`).
- When **selling**, your ask is the higher of:
  - the best other ask in the book (so you stay at the top of the SELL queue), and
  - the best bid marked up by `rate` (so that if your ask is hit, the spread vs. the current bid is at least `rate`).

The price is then snapped to the instrument's price step (`minPriceIncrement`). The strategy may also nudge the price by one step to be strictly better than the chosen "best" when that makes sense.

## Parameters

| Where | Name | Type | Required | Description |
|---|---|---|---|---|
| Path | `instrumentId` | string | yes | Broker UID of the instrument. Get it from [`GET /instrument/any/{ticker}`](../instruments.md). |
| Path | `direction` | enum | yes | `buy` or `sell`. |
| Body | `rate` | number in `(0, 1)` | **yes** | Required spread fraction, e.g. `0.001` for 0.1%. |
| Body | `refreshNotifyPeriod` | ISO-8601 duration | no | If present, enables [auto-refresh](../auto-refresh.md) at that interval, e.g. `PT10M`. |

## Behaviour

| Operation | Effect |
|---|---|
| `start` | Computes the spread-protected best price, places a `LIMIT` order for 1 lot at that price, and registers the auto-refresh notifier if requested. Assignment becomes `IN_PROGRESS`. |
| `refresh` | If the broker reports the order as fully executed → assignment becomes `COMPLETED`. Otherwise recomputes the price using the spread rule against the live order book; if the price has changed, updates the broker order. |
| `cancel` | Cancels the broker order, completes the auto-refresh notifier (if any), and moves the assignment to `COMPLETED`. Terminal. |

## Endpoints

All endpoints require the `X-API-KEY` header.

| Operation | Method | Path | Body |
|---|---|---|---|
| Start | `POST` | `/assignment/fractional-spread/{instrumentId}/start/{direction}` | `{ "rate": 0.001, "refreshNotifyPeriod": "PT10M" }` (`refreshNotifyPeriod` optional) |
| List | `GET` | `/assignment/fractional-spread/` | — |
| Get one | `GET` | `/assignment/fractional-spread/{id}` | — |
| Refresh | `PATCH` | `/assignment/fractional-spread/{id}/refresh` | — |
| Refresh all | `PATCH` | `/assignment/fractional-spread/refresh-all` | — |
| Cancel | `DELETE` | `/assignment/fractional-spread/{id}` | — |

## Response fields

In addition to the common fields described in [the overview](overview.md) (`id`, `iid`, `state`, `refreshNotifier`):

| Field | Meaning |
|---|---|
| `direction` | `BUY` or `SELL`. |
| `rate` | The spread fraction configured at start. |
| `info.orderId` | Broker `id` of the currently active order; look it up via [`GET /order/{id}`](../orders.md). |

## Examples

### Start a buy with a 0.1% spread guarantee and 5-minute auto-refresh

```bash
curl -X POST \
     -H "X-API-KEY: <your-key>" \
     -H "Content-Type: application/json" \
     -d '{ "rate": 0.001, "refreshNotifyPeriod": "PT5M" }' \
     http://<host>/assignment/fractional-spread/<instrumentId>/start/buy
```

Example response:

```json
{
  "id": "8c0f6f4b-3f6a-4d6a-9b6e-2c2c9b1c9b6f",
  "iid": "<instrumentId>",
  "state": "IN_PROGRESS",
  "direction": "BUY",
  "rate": 0.001,
  "info": { "orderId": "<broker-order-id>" },
  "refreshNotifier": {
    "id": "…",
    "properties": { "type": "PERIODIC", "period": "PT5M" },
    "taskId": "…",
    "state": "ACTIVE"
  }
}
```

### Start a sell without auto-refresh (manual control only)

```bash
curl -X POST \
     -H "X-API-KEY: <your-key>" \
     -H "Content-Type: application/json" \
     -d '{ "rate": 0.002 }' \
     http://<host>/assignment/fractional-spread/<instrumentId>/start/sell
```

### Manually refresh

```bash
curl -X PATCH \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/fractional-spread/<id>/refresh
```

### Refresh every in-progress Fractional Spread assignment

```bash
curl -X PATCH \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/fractional-spread/refresh-all
```

### Cancel

```bash
curl -X DELETE \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/fractional-spread/<id>
```

## Related

- [Top Price strategy](top-price.md) — similar, without the spread guarantee.
- [Repeatable Fractional Spread strategy](repeatable-fractional-spread.md) — runs this strategy in an endless flipping loop.
- [Auto-refresh](../auto-refresh.md)
- [API reference](../api-reference.md)
