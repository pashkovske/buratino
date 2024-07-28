package ru.pashkovske.buratino.tinkoff.service.utils;

import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.Comparator;

public class PriceUtils {
    static public Quotation plus(Quotation left, Quotation right) {
        return Quotation.newBuilder()
                .setUnits(left.getUnits() + right.getUnits())
                .setNano(left.getNano() + right.getNano())
                .build();
    }
    static public Quotation minus(Quotation minuend, Quotation subtrahend) {
        return Quotation.newBuilder()
                .setUnits(minuend.getUnits() - subtrahend.getUnits())
                .setNano(minuend.getNano() - subtrahend.getNano())
                .build();
    }
    static public Quotation mul(Quotation price, int multiplier) {
        return Quotation.newBuilder()
                .setUnits(price.getUnits() * multiplier)
                .setNano(price.getNano() * multiplier)
                .build();
    }
    static public Comparator<Quotation> getPriceComparator() {
        return (Quotation left, Quotation right) -> {
            Quotation diff = minus(left, right);
            if (diff.getUnits() != 0) {
                if (diff.getUnits() > 0) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
            else {
                return diff.getNano();
            }
        };
    }
}
