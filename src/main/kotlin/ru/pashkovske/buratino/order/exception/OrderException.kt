package ru.pashkovske.buratino.order.exception

open class OrderException(
    message: String,
    orderId: String? = null,
    cause: Throwable? = null
) : IllegalStateException(
    messageString(
        message = message,
        orderId = orderId
    ),
    cause
) {

    companion object {
        private fun messageString(
            message: String,
            orderId: String?
        ): String {
            if (orderId == null) {
                return super.toString()
            }
            return "Error with order id: $orderId; $message"
        }
    }
}
