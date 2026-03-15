package ru.pashkovske.buratino.assignment.limit.base.controller.mapper

import ru.pashkovske.buratino.assignment.limit.base.controller.dto.OrderInfoDto
import ru.pashkovske.buratino.assignment.limit.base.model.OrderInfo

object OrderInfoMapper {

    fun toDto(orderInfo: OrderInfo): OrderInfoDto {
        return OrderInfoDto(
            orderId = orderInfo.orderId
        )
    }
}
