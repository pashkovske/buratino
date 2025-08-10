package ru.pashkovske.buratino.tinkoff.service.price.dto

import ru.pashkovske.buratino.tinkoff.service.price.model.Price

data class SpreadRequestDto(
    val instrumentUid: String,
    val minPrice: Price,
    val maxPrice: Price
)
