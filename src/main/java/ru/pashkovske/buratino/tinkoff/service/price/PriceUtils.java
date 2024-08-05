package ru.pashkovske.buratino.tinkoff.service.price;

import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.math.BigInteger;
import java.util.Comparator;

public class PriceUtils {
    static private final int MAX_NANO = 1000000000;

    static public Quotation map(MoneyValue price) {
        return Quotation.newBuilder()
                .setUnits(price.getUnits())
                .setNano(price.getNano())
                .build();
    }

    static public Quotation plus(Quotation left, Quotation right) {
        long units = left.getUnits() + right.getUnits();
        int nano = left.getNano() + right.getNano();
        if (nano >= MAX_NANO) {
            units += 1;
            nano -= MAX_NANO;
        }
        return Quotation.newBuilder()
                .setUnits(units)
                .setNano(nano)
                .build();
    }
    static public Quotation minus(Quotation minuend, Quotation subtrahend) {
        long units = minuend.getUnits() - subtrahend.getUnits();
        int nano = minuend.getNano() - subtrahend.getNano();
        if (nano < 0) {
            units -= 1;
            nano += MAX_NANO;
        }
        return Quotation.newBuilder()
                .setUnits(units)
                .setNano(nano)
                .build();
    }
    static public Quotation mul(Quotation price, long multiplier) {
        return bigIntToQuotation(
                quotationToBigInt(price)
                        .multiply(BigInteger.valueOf(multiplier))
        );
    }
    static public long[] div(Quotation dividend, Quotation divisor) {
        BigInteger[] quotient = quotationToBigInt(dividend).divideAndRemainder(quotationToBigInt(divisor));
        return new long[]{quotient[0].longValueExact(), quotient[1].longValueExact()};
    }
    static public long priceInSteps(Quotation price, Quotation step) {
        long[] quotient = div(price, step);
        if (quotient[1] != 0) {
            throw new IllegalArgumentException("Цена:\n" + price + "Не делится нацело на шаг:\n" + step);
        }
        return quotient[0];
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

    private static BigInteger quotationToBigInt(Quotation price) {
        return BigInteger
                .valueOf(price.getUnits())
                .multiply(BigInteger.valueOf(1000000000))
                .add(BigInteger.valueOf(price.getNano()));
    }

    private static Quotation bigIntToQuotation(BigInteger price) {
        BigInteger[] quotient = price.divideAndRemainder(BigInteger.valueOf(MAX_NANO));
        return Quotation.newBuilder()
                .setUnits(quotient[0].longValueExact())
                .setNano(quotient[1].intValueExact())
                .build();
    }
}
