package ru.pashkovske.buratino.order.exception

abstract class OrderApiNotFoundException(
    orderId: String,
    apiName: String,
    cause: Throwable? = null
): OrderApiException(
    message = "Order not found",
    orderId = orderId,
    cause = cause,
    apiName = apiName
)
