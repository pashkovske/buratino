# Missing Assignment Integration Test Cases

Analysis of all 14 assignment integration tests under
`src/test/kotlin/ru/pashkovske/buratino/integration/assignment/` against the production
code in `src/main/kotlin/ru/pashkovske/buratino/assignment/`.

## 1. Current Coverage Matrix (concrete types only)

| Assignment Type            | Create              | Refresh                  | Cancel |
|----------------------------|---------------------|--------------------------|--------|
| TopPrice                   | partial (SELL only) | only "skip on unchanged" | done   |
| FractionalSpread           | BUY only            | only "skip on unchanged" | done   |
| RepeatableFractionalSpread | BUY/SELL            | only "skip on unchanged" | done   |

Note: `BasicAssignmentTest.kt` is an abstract base/helper class, not a test for the
"Basic" assignment type. Abstract base assignments are out of scope (see Scope below).

## Scope

- Only final (concrete) assignment classes need tests. Abstract base assignments
  (`LimitOrderAssignment`, `BasicAssignment`) are out of scope — they have no dedicated
  controller and are exercised only through their concrete subclasses.
- Coverage target: the **main logic** of each concrete assignment type only (no edge cases
  for now). All main-logic scenarios must be covered, including the case where refresh
  actually changes the price (replaces the order).

## 2. Absent Test Cases by Category

### 2.1 Lifecycle — Create (main logic)

- **TopPrice create BUY direction** — concrete tests only exercise SELL.
  `TopPriceAssignmentLimitOrderFactory.getPrice()` uses direction-specific
  `marketPriceService.getTopOfBook()`/`getOneStepOverTopOfBook()`.
- **FractionalSpread create SELL direction** — only BUY tested.
- **RepeatableFractionalSpread create without refresh notifier (BUY and SELL)** — only
  SELL-with-notifier tested. Opposite-direction toggle in
  `RepeatableFractionalSpreadAssignmentRefresher.getOpposite()` must be covered both ways.

### 2.2 Lifecycle — Refresh (main logic)

- **Refresh replaces the order when price changes** — current Refresh tests only assert
  "skip when unchanged". No test verifies that on a price change `replaceOrder` IS called
  and `assignment.info.orderId` is updated to the new order. Missing for all three
  concrete types: TopPrice, FractionalSpread, RepeatableFractionalSpread.

### 2.3 Untested Endpoints (main logic)

- `GET /assignment/top-price/` (getAll)
- `GET /assignment/top-price/{id}` (get)
- `PATCH /assignment/top-price/refresh-all`

These are only used as helpers in other tests, never asserted directly.

## 3. Structural / Naming Inconsistencies (to be fixed)

Make the test suite structure consistent across the three concrete assignment types:

- **Rename `TopPericeTest.kt` → `TopPriceTest.kt`** and fix the typo in the class name.
- **Rename/clarify `BasicAssignmentTest.kt`** — it is a shared base, not a test for the
  Basic assignment type. Rename to something like `AssignmentTestBase.kt` to avoid confusion.
- **Unify direction parametrization** — TopPrice uses `@ParameterizedTest` with
  `orderDirectionAndStepOver`; FractionalSpread and RepeatableFractionalSpread hardcode
  direction. Apply the same parametrized pattern to all three so both BUY and SELL are
  covered for create, refresh, and cancel.
- **Unify cancel direction coverage** — currently TopPrice cancels only SELL while
  FractionalSpread/RepeatableFractionalSpread cancel only BUY. Every type should cover both
  directions in cancel.
- **Unify refresh coverage** — every concrete type should have both the "skip on unchanged"
  refresh test AND a "replace order on price change" refresh test.
