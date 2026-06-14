# API reference

Consolidated reference for every HTTP endpoint Buratino exposes. For conceptual explanations see [Getting started](getting-started.md), [Assignment overview](assignments/overview.md), [Instruments](instruments.md), [Orders](orders.md) and [Auto-refresh](auto-refresh.md).

## Authentication

Every endpoint listed below requires the `X-API-KEY` header:

```
X-API-KEY: <your-key>
```

The following paths are public (no key required) and intended for documentation:

| Path | Purpose |
|---|---|
| `/swagger-ui/**` | Interactive API explorer (Swagger UI). |
| `/v3/api-docs/**` | Raw OpenAPI specification. |

## Common response codes

| Status | When |
|---|---|
| `200 OK` | Successful read / mutation. |
| `401 Unauthorized` | `X-API-KEY` header is missing. |
| `403 Forbidden` | `X-API-KEY` header is present but the value is incorrect. |

Endpoint-specific error codes (e.g. `404` for an unknown id, `400` for a malformed body) are documented in Swagger UI; for the user-facing semantics covered here, `401` and `403` are the headline auth cases.

## Instruments

See [Instruments](instruments.md).

| Operation | Method | Path | Description |
|---|---|---|---|
| Look up by ticker | `GET` | `/instrument/any/{ticker}` | Returns instrument metadata for the given ticker. |

## Orders

See [Orders](orders.md).

| Operation | Method | Path | Description |
|---|---|---|---|
| List all tracked orders | `GET` | `/order/` | Returns every order Buratino is tracking locally. |
| Get one order | `GET` | `/order/{id}` | Returns a single order by broker order id. |

## Assignments

The path template is `/assignment/{strategy}` where `{strategy}` is one of:

| Strategy | Path segment | Doc |
|---|---|---|
| Top Price | `top-price` | [Top Price](assignments/top-price.md) |
| Fractional Spread | `fractional-spread` | [Fractional Spread](assignments/fractional-spread.md) |
| Repeatable Fractional Spread | `repeatable/fractional-spread` | [Repeatable Fractional Spread](assignments/repeatable-fractional-spread.md) |

Every strategy supports the same six operations:

| Operation | Method | Path | Body | Notes |
|---|---|---|---|---|
| Start (create + start) | `POST` | `/assignment/{strategy}/{instrumentId}/start/{direction}` | Strategy-specific (see below) | Assignment is created and immediately put into `IN_PROGRESS`. |
| List | `GET` | `/assignment/{strategy}/` | — | All assignments of this strategy in any state. |
| Get one | `GET` | `/assignment/{strategy}/{id}` | — | Lookup by UUID. |
| Refresh | `PATCH` | `/assignment/{strategy}/{id}/refresh` | — | Manual reconciliation against the live market. |
| Refresh all | `PATCH` | `/assignment/{strategy}/refresh-all` | — | Reconciles every `IN_PROGRESS` assignment of this strategy. |
| Cancel | `DELETE` | `/assignment/{strategy}/{id}` | — | Terminal. Cancels the broker order and stops auto-refresh. |

### Start bodies and query parameters by strategy

#### Top Price (`top-price`)

- Query: `oneStepOver` (boolean, optional, default `false`).
- Body:
  ```json
  { "refreshNotifyPeriod": "PT10M" }
  ```
  `refreshNotifyPeriod` is optional. `{}` is also valid (no auto-refresh).

#### Fractional Spread (`fractional-spread`)

- Body:
  ```json
  { "rate": 0.001, "refreshNotifyPeriod": "PT5M" }
  ```
  `rate` is **required** (number in `(0, 1)`). `refreshNotifyPeriod` is optional.

#### Repeatable Fractional Spread (`repeatable/fractional-spread`)

- Body: identical to Fractional Spread.
- `direction` in the path is the **initial** direction; the cycle flips it on every fill.

### Common assignment response fields

| Field | Meaning |
|---|---|
| `id` | UUID assigned by Buratino. |
| `iid` | `instrumentId` the assignment operates on. |
| `state` | `QUEUED` / `IN_PROGRESS` / `COMPLETED`. |
| `refreshNotifier` | `null` or `{ id, properties: { type: "PERIODIC", period }, taskId, state }`. |

Plus strategy-specific fields:

| Strategy | Extra fields |
|---|---|
| Top Price | `direction`, `oneStepOver`, `info.orderId` |
| Fractional Spread | `direction`, `rate`, `info.orderId` |
| Repeatable Fractional Spread | `child` (a full Fractional Spread assignment) |

## End-to-end example with `curl`

```bash
# 1. Look up an instrument by ticker
curl -H "X-API-KEY: <your-key>" \
     http://<host>/instrument/any/SBER

# 2. Start a Top Price sell with 10-minute auto-refresh, one step over the best ask
curl -X POST \
     -H "X-API-KEY: <your-key>" \
     -H "Content-Type: application/json" \
     -d '{ "refreshNotifyPeriod": "PT10M" }' \
     'http://<host>/assignment/top-price/<instrumentId>/start/sell?oneStepOver=true'

# 3. Inspect it
curl -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/<assignmentId>

# 4. Force a refresh
curl -X PATCH \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/<assignmentId>/refresh

# 5. Check the broker order it placed
curl -H "X-API-KEY: <your-key>" \
     http://<host>/order/<orderId>

# 6. Cancel
curl -X DELETE \
     -H "X-API-KEY: <your-key>" \
     http://<host>/assignment/top-price/<assignmentId>
```

## Related

- [Getting started](getting-started.md)
- [Assignment overview](assignments/overview.md)
- [Auto-refresh](auto-refresh.md)
- [Persistence and recovery](persistence-and-recovery.md)
