package ru.pashkovske.buratino.tinkoff.service.price.mapper;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.FutureWrapper;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentWrapper;
import ru.pashkovske.buratino.tinkoff.service.price.PriceUtils;
import ru.tinkoff.piapi.contract.v1.*;

public class PriceMapper {
    @NonNull
    public static MoneyValue map(@NonNull Quotation price, @NonNull InstrumentWrapper instrument) {
        Quotation moneyPrice = getMoneyPriceQuotation(price, instrument);
        return MoneyValue.newBuilder()
                .setUnits(moneyPrice.getUnits())
                .setNano(moneyPrice.getNano())
                .setCurrency(instrument.getCurrency())
                .build();
    }

    @NonNull
    private static Quotation getMoneyPriceQuotation(@NonNull Quotation price, @NonNull InstrumentWrapper instrument) {
        if (instrument.getType().equals(InstrumentType.INSTRUMENT_TYPE_FUTURES)) {
            FutureWrapper future = (FutureWrapper) instrument;
            long[] quotient = PriceUtils.div(price, future.getMinPriceIncrement());
            assert quotient[1] == 0;
            return PriceUtils.mul(future.getMinPriceIncrementAmount(), quotient[0]);
        }
        else {
            return price;
        }
    }

    @NonNull
    public static Quotation mapMoneyToPtsQuotation(@NonNull Quotation price, @NonNull InstrumentWrapper instrument) {
        if (instrument.getType().equals(InstrumentType.INSTRUMENT_TYPE_FUTURES)) {
            FutureWrapper future = (FutureWrapper) instrument;
            long[] quotient = PriceUtils.div(price, future.getMinPriceIncrementAmount());
            assert quotient[1] == 0;
            return PriceUtils.mul(future.getMinPriceIncrement(), quotient[0]);
        }
        else {
            return price;
        }
    }
}
