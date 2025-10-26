package ru.pashkovske.buratino.integration.mock

import org.mockito.Mockito
import org.springframework.stereotype.Service
import ru.pashkovske.buratino.order.adapter.ExtOrderServiceAdapter
import ru.pashkovske.buratino.order.model.Order
import ru.pashkovske.buratino.order.model.OrderInstantInfo
import ru.pashkovske.buratino.order.model.limit.LimitOrderRequest
import ru.pashkovske.buratino.util.loader.FileLoader

@Service
class ExtOrderServiceMocker(
    private val mock: ExtOrderServiceAdapter
) {
    fun addCreateMock(
        pathRequest: String,
        pathResponse: String
    ) {
        Mockito.`when`(
            mock.createOrder(
                FileLoader.loadFromJson(pathRequest, LimitOrderRequest::class.java)
            )
        ).thenReturn(FileLoader.loadFromJson(pathResponse, Order::class.java))
    }

    fun addReplaceMock(
        orderId: String,
        pathRequest: String,
        pathResponse: String
    ) {
        Mockito.`when`(
            mock.replaceOrder(
                orderId,
                FileLoader.loadFromJson(pathRequest, LimitOrderRequest::class.java)
            )
        ).thenReturn(FileLoader.loadFromJson(pathResponse, Order::class.java))
    }

    fun addGetOrderActualInfoMock(
        orderId: String,
        pathResponse: String
    ) {
        Mockito.`when`(mock.getOrderActualInfo(orderId))
            .thenReturn(FileLoader.loadFromJson(pathResponse, OrderInstantInfo::class.java))
    }

    fun addCancelMock(orderId: String) {
        Mockito.`when`(mock.cancelOrder(orderId))
            .thenAnswer { }
    }
}
