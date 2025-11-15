package ru.pashkovske.buratino.price.model

import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.quotation.model.QuotationSpread

class Spread(
    bid: Quotation?,
    ask: Quotation?,
    val currency: Currency
): QuotationSpread(
    bid = bid,
    ask = ask
) {
    constructor(
        quotationSpread: QuotationSpread,
        currency: Currency
    ): this(
        bid = quotationSpread.bid,
        ask = quotationSpread.ask,
        currency = currency
    )

    fun getMoneyBid(): Price? {
        return bid?.let {
            Price(
                quotation = it,
                currency = currency
            )
        }
    }

    fun getMoneyAsk(): Price? {
        return ask?.let {
            Price(
                quotation = it,
                currency = currency
            )
        }
    }
}