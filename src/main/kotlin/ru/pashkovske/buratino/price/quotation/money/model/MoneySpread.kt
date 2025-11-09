package ru.pashkovske.buratino.price.quotation.money.model

import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.pashkovske.buratino.price.quotation.model.Spread

class MoneySpread(
    bid: Quotation?,
    ask: Quotation?,
    val currency: Currency
): Spread(
    bid = bid,
    ask = ask
) {
    constructor(
        spread: Spread,
        currency: Currency
    ): this(
        bid = spread.bid,
        ask = spread.ask,
        currency = currency
    )

    fun getMoneyBid(): MoneyPrice? {
        return bid?.let {
            MoneyPrice(
                quotation = it,
                currency = currency
            )
        }
    }

    fun getMoneyAsk(): MoneyPrice? {
        return ask?.let {
            MoneyPrice(
                quotation = it,
                currency = currency
            )
        }
    }
}