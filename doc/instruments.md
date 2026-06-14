# Instruments

An **instrument** is a tradable security on the broker — either a **share** or a **future**. Every Buratino assignment is created for exactly one instrument, identified by its broker-issued `instrumentId`.

## Fields

Every instrument returned by Buratino has the following fields:

| Field                  | Type    | Meaning                                                                                                          |
|------------------------|---------|------------------------------------------------------------------------------------------------------------------|
| `instrumentId`         | string  | Broker-issued unique identifier. This is what you pass to assignment `start` endpoints.                          |
| `ticker`               | string  | Short human-readable code (e.g. `SBER`).                                                                         |
| `name`                 | string  | Full human-readable name.                                                                                        |
| `type`                 | enum    | `SHARE` or `FUTURE`.                                                                                             |
| `currency`             | enum    | `RUB`, `USD`, `EUR`, or `UNKNOWN`. Currency in which the instrument is priced.                                   |
| `lot`                  | integer | Number of base units per lot. Assignments always operate on whole lots.                                          |
| `minPriceIncrement`    | price   | Smallest allowed price step on the broker for this instrument. Buratino uses this when `oneStepOver` is enabled. |
| `minPriceIncrementPts` | price   | *(futures only)* Minimum price increment expressed in points.                                                    |
| `isTradable`           | boolean | Whether the instrument is currently tradable on the broker.                                                      |

### Prices

Prices throughout Buratino are represented as a `unit.nano` pair plus a `currency` code:

- `unit` — integer part of the price.
- `nano` — fractional part in nano-units (range `0 .. 1_000_000_000`, where `1_000_000_000` equals one whole unit).
- `currency` — one of `RUB`, `USD`, `EUR`, `UNKNOWN`.

For example, a price of `123.45 RUB` is `{ "unit": 123, "nano": 450000000, "currency": "RUB" }`.

## Looking up an instrument

You typically don't memorise `instrumentId` values. Instead, you look an instrument up by its ticker.

### `GET /instrument/any/{ticker}`

Returns the instrument metadata for the given ticker, regardless of whether it's a share or a future.

```bash
curl -H "X-API-KEY: <your-key>" \
     http://<host>/instrument/any/SBER
```

The response is the instrument object described above. Copy its `instrumentId` and use it to start an [assignment](assignments/overview.md).

## Things to know

- **Whole lots only.** All assignments place orders for **1 lot**, not 1 unit. The `lot` field on the instrument tells you how many base units that is.
- **Price step matters.** The `oneStepOver` parameter on price-based strategies steps the quoted price by exactly `minPriceIncrement`. Choose strategies and instruments accordingly.

## Where prices come from

Buratino reads the live **order book** for the instrument from the broker. The order book contains all currently outstanding BUY and SELL offers from the market. Assignments inspect this book to decide what price to quote (see [Top Price](assignments/top-price.md) and [Fractional Spread](assignments/fractional-spread.md)).

## Related

- [Orders](orders.md) — the broker-side orders that assignments create for an instrument.
- [Assignment overview](assignments/overview.md) — how strategies use instruments.
