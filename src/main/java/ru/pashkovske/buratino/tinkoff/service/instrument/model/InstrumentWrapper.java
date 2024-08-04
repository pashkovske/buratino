package ru.pashkovske.buratino.tinkoff.service.instrument.model;

import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.SecurityTradingStatus;

public interface InstrumentWrapper {
    InstrumentId getId();
    String getFigi();
    String getName();
    String getTicker();
    Quotation getMinPriceIncrement();
    String getCurrency();
    int getLot();
    InstrumentType getType();
    boolean getForQualInvestorFlag();
    SecurityTradingStatus getTradingStatus();
    default boolean isTradableNow() {
        SecurityTradingStatus status = getTradingStatus();
        return status.equals(SecurityTradingStatus.SECURITY_TRADING_STATUS_NORMAL_TRADING)
                || status.equals(SecurityTradingStatus.SECURITY_TRADING_STATUS_SESSION_OPEN)
                || status.equals(SecurityTradingStatus.SECURITY_TRADING_STATUS_DEALER_NORMAL_TRADING);
    }
}
