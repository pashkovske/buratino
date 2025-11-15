package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.PointsPrice
import ru.pashkovske.buratino.price.model.Price
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.contract.v1.Quotation

object TinkoffPriceMapper {

    fun map(tinkoffMoneyValue: MoneyValue): Price {
        return Price(
            units = tinkoffMoneyValue.units,
            nano = tinkoffMoneyValue.nano,
            currency = Currency.fromStr(tinkoffMoneyValue.currency)
        )
    }

    fun map(
        tinkoffQuotation: Quotation,
        currency: Currency
    ): Price {
        return Price(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano,
            currency = currency
        )
    }

    fun map(
        tinkoffQuotation: Quotation,
        currency: String
    ): Price {
        return Price(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano,
            currency = Currency.fromStr(currency)
        )
    }

    fun mapToPointsPrice(
        tinkoffQuotation: Quotation
    ): PointsPrice {
        return PointsPrice(
            unit = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano
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

    fun mapToQuotation(price: Price): Quotation {
        return Quotation.newBuilder()
            .setUnits(price.units)
            .setNano(price.nano)
            .build()
    }
}
