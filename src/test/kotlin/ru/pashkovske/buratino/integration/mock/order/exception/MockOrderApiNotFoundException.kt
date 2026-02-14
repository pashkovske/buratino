package ru.pashkovske.buratino.integration.mock.order.exception

import ru.pashkovske.buratino.order.exception.OrderApiNotFoundException

class MockOrderApiNotFoundException(
    orderId: String
) : OrderApiNotFoundException(
    orderId = orderId,
    apiName = "Mock"
)
