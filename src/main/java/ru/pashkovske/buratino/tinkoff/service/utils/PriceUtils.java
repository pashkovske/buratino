package ru.pashkovske.buratino.tinkoff.service.utils;

import ru.tinkoff.piapi.contract.v1.Quotation;

public class PriceUtils {
    static public Quotation plus(Quotation left, Quotation right) {
        return Quotation.newBuilder()
                .setUnits(left.getUnits() + right.getUnits())
                .setNano(left.getNano() + right.getNano())
                .build();
    }
    static public Quotation minus(Quotation left, Quotation right) {
        return Quotation.newBuilder()
                .setUnits(left.getUnits() - right.getUnits())
                .setNano(left.getNano() - right.getNano())
                .build();
    }
    static public Quotation mul(Quotation price, int multiplier) {
        return Quotation.newBuilder()
                .setUnits(price.getUnits() * multiplier)
                .setNano(price.getNano() * multiplier)
                .build();
    }
}
