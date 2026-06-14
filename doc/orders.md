# Orders

An **order** is a request that Buratino has sent to the broker on your behalf as part of running an [assignment](assignments/overview.md). You normally do not place orders directly — assignments create, update and cancel them for you. The order endpoints in Buratino are **read-only** and exist for inspection and verification.

## What an order looks like

Every order tracked locally by Buratino has the following high-level fields:

| Field | Type | Meaning |
|---|---|---|
| `id` | string | Broker-issued order identifier. |
| `iid` | string | `instrumentId` of the security this order is for. |
| `request` | object | The original request that was submitted to the broker (price, quantity, direction, type). |
| `commitResult` | object | The broker's response to the submission (acknowledgement and broker-side metadata). |
| `currentInfo` | object | The latest known state of the order on the broker (executed quantity, current status, etc.). |

### Order attributes

| Attribute | Values | Meaning |
|---|---|---|
| **State** | `ACTIVE`, `COMPLETED` | Whether the order is still live on the broker (`ACTIVE`) or finished — fully filled or cancelled (`COMPLETED`). |
| **Type** | `LIMIT`, `MARKET` | `LIMIT` orders have a target price and only execute at that price or better. `MARKET` orders execute immediately at the best available price. |
| **Direction** | `BUY`, `SELL` | Whether the order buys or sells the instrument. |

> All current Buratino strategies place **`LIMIT`** orders.

## Inspection endpoints

Both endpoints require the `X-API-KEY` header.

### `GET /order/`

Lists every order Buratino is tracking locally, in any state.

```bash
curl -H "X-API-KEY: <your-key>" \
     http://<host>/order/
```

### `GET /order/{id}`

Fetches a single order by its broker `id`. The `id` you pass here is the same one stored on the broker side and returned by the list endpoint and by assignment responses (in their `info.orderId` field).

```bash
curl -H "X-API-KEY: <your-key>" \
     http://<host>/order/<broker-order-id>
```

## Relationship with assignments

- When you `start` an assignment, Buratino places an initial `LIMIT` order for **1 lot** on the broker. Its id appears in the assignment response as `info.orderId` (for [Top Price](assignments/top-price.md) and [Fractional Spread](assignments/fractional-spread.md)) or inside the `child` for [Repeatable Fractional Spread](assignments/repeatable-fractional-spread.md).
- When you `refresh` an assignment and the desired price has changed, Buratino updates the order on the broker — possibly by cancelling and re-placing it. The order id visible on the assignment may therefore change over time; always re-fetch the assignment to get the current `orderId`.
- When you `cancel` an assignment, Buratino cancels its currently active broker order. The order then transitions to `COMPLETED`.
- When the broker reports the order as fully executed, a subsequent assignment `refresh` will detect this and complete the assignment.

## Why this is read-only

Buratino is a strategy engine, not a manual order terminal. Mutations are expressed as **assignments**: change what you want by creating, refreshing or cancelling assignments. The order endpoints exist so you can verify *that* Buratino did the right thing on the broker side.

## Related

- [Instruments](instruments.md)
- [Assignment overview](assignments/overview.md)
