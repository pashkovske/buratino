package ru.pashkovske.buratino.order.model

import ru.pashkovske.buratino.instrument.model.InstrumentId

data class Order(
    val id: String,
    val iid: InstrumentId,
    val request: OrderRequest,
    val commitResult: OrderCommitResult,
    var currentInfo: OrderInstantInfo
)