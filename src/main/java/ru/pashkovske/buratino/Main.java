package ru.pashkovske.buratino;

import ru.pashkovske.buratino.service.account.TinkoffAccountResolver;
import ru.pashkovske.buratino.service.instrument.TinkoffShareSelector;
import ru.pashkovske.buratino.service.order.OrderService;
import ru.pashkovske.buratino.service.order.TinkoffOrderService;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.*;

public class Main {
    public static void main(String[] args) {
        String fullAccessToken = System.getenv("TINKOFF_API_TOKEN");
        InvestApi investApi = InvestApi.create(fullAccessToken);

        Share mtsShare = new TinkoffShareSelector(investApi.getInstrumentsService()).findSingleByName("МТС");
        String accountId = new TinkoffAccountResolver("Основной брокерский счет", investApi.getUserService()).getBrokerAccountId();
        OrderService orderService = new TinkoffOrderService(accountId, investApi.getMarketDataService(), investApi.getOrdersService());
        orderService.sellBestByOrderBook(mtsShare);
    }
}