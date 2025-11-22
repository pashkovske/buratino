package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.price.model.Currency
import ru.pashkovske.buratino.price.model.PointsPrice
import ru.pashkovske.buratino.price.model.Price
import ru.tinkoff.piapi.contract.v1.MoneyValue
import ru.tinkoff.piapi.contract.v1.Quotation

private val DUMMY_CURRENCY = Currency.RUB

object TinkoffPriceMapper {

    fun map(tinkoffMoneyValue: MoneyValue): Price {
        return Price(
            unit = tinkoffMoneyValue.units,
            nano = tinkoffMoneyValue.nano,
            currency = Currency.fromStr(tinkoffMoneyValue.currency)
        )
    }

    fun map(
        tinkoffQuotation: Quotation,
        currency: Currency
    ): Price {
        return Price(
            unit = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano,
            currency = currency
        )
    }

    fun map(
        tinkoffQuotation: Quotation,
        currency: String
    ): Price {
        return map(
            tinkoffQuotation = tinkoffQuotation,
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
            .setUnits(price.unit)
            .setNano(price.nano)
            .setCurrency(price.currency.name)
            .build()
    }

    fun mapToQuotation(price: Price): Quotation {
        return Quotation.newBuilder()
            .setUnits(price.unit)
            .setNano(price.nano)
            .build()
    }

    fun convertPtsToMoney(
        pts: Quotation,
        minPtsInc: PointsPrice,
        minPriceInc: Price
    ): Price {
        val ptsCasted = Price(
            unit = pts.units,
            nano = pts.nano,
            currency = DUMMY_CURRENCY
        )
        val minPtsIncrementCasted = Price(
            unit = minPtsInc.unit,
            nano = minPtsInc.nano,
            currency = DUMMY_CURRENCY
        )
        if (ptsCasted % minPtsIncrementCasted != 0L) {
            throw IllegalArgumentException("PointsPrice $pts is not divisible by minimum increment $minPtsInc")
        }
        val minIncsInPrice: Long = ptsCasted / minPtsIncrementCasted
        return minPriceInc * minIncsInPrice
    }
}
