package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.Price
import ru.pashkovske.buratino.price.quotation.model.PtsPrice
import ru.pashkovske.buratino.price.quotation.model.Quotation
import ru.tinkoff.piapi.contract.v1.MoneyValue

object TinkoffPriceMapper {
    fun map(tinkoffQuotation: ru.tinkoff.piapi.contract.v1.Quotation): Quotation {
        return Quotation(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano
        )
    }

    fun mapToPoints(tinkoffQuotation: ru.tinkoff.piapi.contract.v1.Quotation): PtsPrice {
        return PtsPrice(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano
        )
    }

    fun map(tinkoffMoneyValue: MoneyValue): Price {
        return Price(
            units = tinkoffMoneyValue.units,
            nano = tinkoffMoneyValue.nano,
            currency = Currency.fromStr(tinkoffMoneyValue.currency)
        )
    }

    fun map(
        tinkoffQuotation: ru.tinkoff.piapi.contract.v1.Quotation,
        currency: String
    ): Price {
        return Price(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano,
            currency = Currency.fromStr(currency)
        )
    }

    fun map(price: Price): MoneyValue {
        if (price.currency == Currency.UNKNOWN) {
            throw IllegalArgumentException("Unknown currency")
        }
        return MoneyValue.newBuilder()
            .setUnits(price.units)
            .setNano(price.nano)
            .setCurrency(price.currency.name)
            .build()
    }

    fun map(quotation: Quotation): ru.tinkoff.piapi.contract.v1.Quotation {
        return ru.tinkoff.piapi.contract.v1.Quotation.newBuilder()
            .setUnits(quotation.units)
            .setNano(quotation.nano)
            .build()
    }
}
