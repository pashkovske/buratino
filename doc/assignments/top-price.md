# Top Price strategy

> **Goal:** sell or buy **1 lot** of the instrument at the best price currently visible in the order book.

This strategy places a limit order on the same side of the book as you (BUY for buying, SELL for selling) at the best
price already quoted there — optionally one price step better.

## How "best" is defined

- **Selling (`sell`)** — the **highest** price among other SELL orders currently in the order book.
- **Buying (`buy`)** — the **lowest** price among other BUY orders currently in the order book.

> Note: "other" means orders other than your own assignment's order. Buratino does not compete against itself when
> recomputing the best price.

### The `oneStepOver` flag

A boolean query parameter (default `false`).

| `oneStepOver` | Behaviour                                                                                                                                                            |
|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `false`       | Quote at exactly the current best price on your side of the book.                                                                                                    |
| `true`        | Quote at one `minPriceIncrement` better than the current best. For `sell` that means lower (more aggressive ask); for `buy` that means higher (more aggressive bid). |

## Parameters

| Where | Name                  | Type              | Required             | Description                                                                                    |
|-------|-----------------------|-------------------|----------------------|------------------------------------------------------------------------------------------------|
| Path  | `instrumentId`        | string            | yes                  | Broker UID of the instrument. Get it from [`GET /instrument/any/{ticker}`](../instruments.md). |
| Path  | `direction`           | enum              | yes                  | `buy` or `sell`.                                                                               |
| Query | `oneStepOver`         | boolean           | no (default `false`) | Step one price increment ahead of the best price.                                              |
| Body  | `refreshNotifyPeriod` | ISO-8601 duration | no                   | If present, enables [auto-refresh](../auto-refresh.md) at that interval, e.g. `PT10M`.         |

The body is JSON. To start without auto-refresh, send `{}`.

## Behaviour

| Operation | Effect                                                                                                                                                                                                                    |
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `start`   | Places a `LIMIT` order for 1 lot at the computed best price (optionally one step over). Registers the auto-refresh notifier if requested. Assignment becomes `IN_PROGRESS`.                                               |
| `refresh` | If the broker reports the order as fully executed → assignment becomes `COMPLETED`. Otherwise recomputes the best price from the live order book; if it differs from the current order's price, updates the broker order. |
| `cancel`  | Cancels the broker order, completes the auto-refresh notifier (if any), and moves the assignment to `COMPLETED`. Terminal.                                                                                                |

## Endpoints

All endpoints require the `X-API-KEY` header.

| Operation   | Method   | Path                                                                        | Body                                         |
|-------------|----------|-----------------------------------------------------------------------------|----------------------------------------------|
| Start       | `POST`   | `/assignment/top-price/{instrumentId}/start/{direction}?oneStepOver={bool}` | `{ "refreshNotifyPeriod": "PT10M" }` or `{}` |
| List        | `GET`    | `/assignment/top-price/`                                                    | —                                            |
| Get one     | `GET`    | `/assignment/top-price/{id}`                                                | —                                            |
| Refresh     | `PATCH`  | `/assignment/top-price/{id}/refresh`                                        | —                                            |
| Refresh all | `PATCH`  | `/assignment/top-price/refresh-all`                                         | —                                            |
| Cancel      | `DELETE` | `/assignment/top-price/{id}`                                                | —                                            |

## Response fields

In addition to the common fields described in [the overview](overview.md) (`id`, `iid`, `state`, `refreshNotifier`), Top
Price assignments expose:

| Field          | Meaning                                                                                      |
|----------------|----------------------------------------------------------------------------------------------|
| `direction`    | `BUY` or `SELL`.                                                                             |
| `oneStepOver`  | The value used when the assignment was started.                                              |
| `info.orderId` | Broker `id` of the currently active order; look it up via [`GET /order/{id}`](../orders.md). |

## Examples

### Start a Top Price sell assignment with 10-minute auto-refresh, one step over the best ask

```bash
curl -X POST \
     -H "X-API-KEY: <your-key>" \
     -H "Content-Type: application/json" \
     -d '{ "refreshNotifyPeriod": "PT10M" }' \
     'http://<host>/assignment/top-price/<instrumentId>/start/sell?oneStepOver=true'
```

Example response:

```json
{
  "id": "1c2b0c41-9a32-4d6a-9b6e-2c2c9b1c9b6f",
  "iid": "<instrumentId>",
  "state": "IN_PROGRESS",
  "direction": "SELL",
  "oneStepOver": true,
  "info": {
    "orderId": "<broker-order-id>"
  },
  "refreshNotifier": {
    "id": "5b66f9ee-49e5-4c9d-9c8e-c1c9c5e6a5f1",
    "properties": {
      "type": "PERIODIC",
      "period": "PT10M"
    },
    "taskId": "…",
    "state": "ACTIVE"
  }
}
```

### Manually refresh that assignment

```bash
curl -X PATCH \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/1c2b0c41-9a32-4d6a-9b6e-2c2c9b1c9b6f/refresh
```

### Refresh every in-progress Top Price assignment

```bash
curl -X PATCH \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/refresh-all
```

### Cancel

```bash
curl -X DELETE \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/1c2b0c41-9a32-4d6a-9b6e-2c2c9b1c9b6f
```
