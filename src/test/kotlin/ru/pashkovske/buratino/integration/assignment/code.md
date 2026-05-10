# Assignment tests

По самим Assignments см. документацию
в [main](../../../../../../../main/kotlin/ru/pashkovske/buratino/assignment/code.md).

## Структура

Тесты повторяют структуру самих Assignments.
На месте абстрактных классов могут быть только классы с вспомогательными protected методами, отвечающими за операции над
поручениями.
Все тесты наследуются от [BasicAssignmentTest](BasicAssignmentTest.kt).
1 класс для кейсов каждой проверяемой операции над поручениями:

- `create`
- `refresh`
- `cancel`
- `coninue`

```mermaid
classDiagram
    class BasicAssignmentTest {
        <<abstract>>
        # assertAllAssignmentsCancelled(path: String, expectedCount: Int)
        # assertAllOrdersCancelled(expectedCount: Int)
        # expectOrder(orderId: String, expected: LimitOrderRequest)
    }
    class TopPericeTest {
        <<abstract>>
        Basic for all top price assignment operation tests
        # create(...): MvcResult
        # refresh(assignmentId: UUID): MvcResult
        # cancel(assignmentId: UUID): MvcResult
    }
    class FractionalSpreadTest {
        <<abstract>>
        Basic for all fractional spread assignment operation tests
        # create(...): MvcResult
        # refresh(assignmentId: UUID): MvcResult
        # cancel(assignmentId: UUID): MvcResult
    }

    BasicAssignmentTest <|-- TopPericeTest
    BasicAssignmentTest <|-- FractionalSpreadTest
```