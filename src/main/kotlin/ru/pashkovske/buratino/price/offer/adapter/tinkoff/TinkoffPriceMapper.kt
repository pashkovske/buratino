package ru.pashkovske.buratino.price.offer.adapter.tinkoff

import ru.pashkovske.buratino.price.money.model.Currency
import ru.pashkovske.buratino.price.money.model.MoneyPrice
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

    fun map(tinkoffMoneyValue: MoneyValue): MoneyPrice {
        return MoneyPrice(
            units = tinkoffMoneyValue.units,
            nano = tinkoffMoneyValue.nano,
            currency = Currency.fromStr(tinkoffMoneyValue.currency)
        )
    }

    fun map(
        tinkoffQuotation: ru.tinkoff.piapi.contract.v1.Quotation,
        currency: String
    ): MoneyPrice {
        return MoneyPrice(
            units = tinkoffQuotation.units,
            nano = tinkoffQuotation.nano,
            currency = Currency.fromStr(currency)
        )
    }

    fun map(moneyPrice: MoneyPrice): MoneyValue {
        if (moneyPrice.currency == Currency.UNKNOWN) {
            throw IllegalArgumentException("Unknown currency")
        }
        return MoneyValue.newBuilder()
            .setUnits(moneyPrice.units)
            .setNano(moneyPrice.nano)
            .setCurrency(moneyPrice.currency.name)
            .build()
    }

    fun map(quotation: Quotation): ru.tinkoff.piapi.contract.v1.Quotation {
        return ru.tinkoff.piapi.contract.v1.Quotation.newBuilder()
            .setUnits(quotation.units)
            .setNano(quotation.nano)
            .build()
    }
}
