package ru.pashkovske.buratino.assignment.controller.mapper

import ru.pashkovske.buratino.assignment.controller.dto.limit.order.OrderInfoDto
import ru.pashkovske.buratino.assignment.model.limit.order.OrderInfo

object OrderInfoMapper {

    fun toDto(orderInfo: OrderInfo): OrderInfoDto {
        return OrderInfoDto(
            orderId = orderInfo.orderId
        )
    }
}