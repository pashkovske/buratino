package ru.pashkovske.buratino.order.exception

abstract class OrderApiException(
    message: String,
    orderId: String? = null,
    cause: Throwable? = null,
    apiName: String
) : OrderException(
    message = messageString(
        message = message,
        apiName = apiName
    ),
    orderId = orderId,
    cause = cause
) {

    companion object {
        private fun messageString(
            message: String,
            apiName: String
        ): String {
            return "Error in API: $apiName; $message"
        }
    }
}
