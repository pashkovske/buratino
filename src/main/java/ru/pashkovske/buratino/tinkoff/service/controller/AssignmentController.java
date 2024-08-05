package ru.pashkovske.buratino.tinkoff.service.controller;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.RobotExploitSpread;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.OtherRobotExploitWithSpread;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.List;

@RequiredArgsConstructor
public class AssignmentController {
    private final FollowBestPrice followBestStrategy;
    private final RobotExploitSpread exploitStrategy;
    private final CurrentAccountOrders currentAccountOrders;
    private final InstrumentSelector selector;

    public void pullAllForBestPrice() {
        List<OrderState> orders = currentAccountOrders.getAllOrders();
        System.out.println(followBestStrategy.pull(orders));
    }

    public void pingBestPrice() {
        AssignmentCommand command;
        List<Assignment> assignments = followBestStrategy.refreshAll();
        System.out.println("\n\n");
        for (Assignment assignment : assignments) {
            command = assignment.getCommand();
            OrderResponse order = assignment.getOrders().getFirst().getOrderResponse();

            System.out.println("| " +
                    command.getInstrument().getTicker() + "\t|\t" +
                    order.time() + "\t|\t" +
                    order.price().getUnits() + "." + order.price().getNano() + "\t|\t" +
                    order.direction() + "\t|\t" +
                    command.getInstrument().getName() + "\t|\t" +
                    order.commission().getUnits() + "." + order.commission().getNano()
            );
        }
    }

    public void postBuy(List<String> tickers) {
        AssignmentCommand command;
        for (String ticker : tickers) {
            command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
            followBestStrategy.post(command);
        }
    }

    public void postExploit(
            String ticker,
            long lots,
            long minAskUnits, int minAskNano,
            long maxBidUnits, int maxBidNano
    ) {
        AssignmentCommand command = new OtherRobotExploitWithSpread(
                selector.getByTicker("NKNCP"),
                2,
                Quotation.newBuilder().setUnits(69).setNano(840000000).build(),
                Quotation.newBuilder().setUnits(69).setNano(660000000).build()
        );
        exploitStrategy.post(command);
    }
}
