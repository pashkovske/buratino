package ru.pashkovske.buratino.tinkoff.service.assignment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pashkovske.buratino.tinkoff.service.account.CurrentAccountOrders;
import ru.pashkovske.buratino.tinkoff.service.assignment.mapper.AssignmentMapper;
import ru.pashkovske.buratino.tinkoff.service.assignment.model.AssignmentStateDto;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.FollowBestPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.RobotExploitSpread;
import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestSellPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.OtherRobotExploitWithSpread;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@RestController
@RequiredArgsConstructor
public class AssignmentController {
    private final FollowBestPrice followBestStrategy;
    private final RobotExploitSpread exploitStrategy = null;
    private final CurrentAccountOrders currentAccountOrders;
    private final InstrumentSelector selector;

    @PostMapping("/strategy/follow-best-price/pull")
    public List<AssignmentStateDto> pullAllForBestPrice() {
        List<OrderState> orders = currentAccountOrders.getAllOrders();
        return followBestStrategy.pull(orders).stream()
                .map(AssignmentMapper::mapBestPrice)
                .toList();
    }

    @PostMapping("/schedule-api/strategy/follow-best-price/pull")
    public List<AssignmentStateDto> pullAndScheduleAllForBestPrice() {
        List<OrderState> orders = currentAccountOrders.getAllOrders();
        return followBestStrategy.pullAndSchedule(orders).stream()
                .map(AssignmentMapper::mapBestPrice)
                .toList();
    }

    @PostMapping("/strategy/follow-best-price/ping-all")
    public List<AssignmentStateDto> pingAllBestPrice() {
        return followBestStrategy.refreshAll()
                .stream()
                .map(AssignmentMapper::mapBestPrice)
                .toList();
    }

    @PostMapping("/instrument/{ticker}/assignment/follow-best-price/buy")
    public AssignmentStateDto postBuy(@PathVariable String ticker) {
        AssignmentCommand command;
        command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
        Assignment assignment = followBestStrategy.post(command);
        return AssignmentMapper.mapBestPrice(assignment);
    }

    @PostMapping("/instrument/{ticker}/assignment/follow-best-price/sell")
    public AssignmentStateDto postSell(@PathVariable String ticker) {
        AssignmentCommand command;
        command = new FollowBestSellPrice(selector.getByTicker(ticker), 1);
        Assignment assignment = followBestStrategy.post(command);
        return AssignmentMapper.mapBestPrice(assignment);
    }

    @PostMapping("/schedule-api/instrument/{ticker}/assignment/follow-best-price/buy")
    public AssignmentStateDto scheduleBuy(@PathVariable String ticker) {
        AssignmentCommand command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
        Assignment assignment = followBestStrategy.post(command);
        followBestStrategy.scheduleRefresh(assignment.getId());
        return AssignmentMapper.mapBestPrice(assignment);
    }

    @PostMapping("/schedule-api/instrument/{ticker}/assignment/follow-best-price/sell")
    public AssignmentStateDto scheduleSell(@PathVariable String ticker) {
        AssignmentCommand command;
        command = new FollowBestSellPrice(selector.getByTicker(ticker), 1);
        Assignment assignment = followBestStrategy.post(command);
        followBestStrategy.scheduleRefresh(assignment.getId());
        return AssignmentMapper.mapBestPrice(assignment);
    }

    @DeleteMapping("/schedule-api/instrument/assignments/{assignmentId}")
    public void cancel(@PathVariable UUID assignmentId) {
        followBestStrategy.cancel(assignmentId);
    }

    @GetMapping("/instrument/assignments")
    public List<AssignmentStateDto> getAllAssignments() {
        return followBestStrategy.getAll()
                .stream()
                .map(AssignmentMapper::mapBestPrice)
                .toList();
    }

    public void postBuyList(List<String> tickers) {
        AssignmentCommand command;
        for (String ticker : tickers) {
            command = new FollowBestBuyPrice(selector.getByTicker(ticker), 1);
            followBestStrategy.post(command);
        }
    }

    @PostMapping("/instrument/{ticker}/assignment/robot-exploit")
    public void postExploit(
            @PathVariable String ticker,
            @RequestBody long lots,
            @RequestBody long minAskUnits, @RequestBody int minAskNano,
            @RequestBody long maxBidUnits, @RequestBody int maxBidNano
    ) {
        AssignmentCommand command = new OtherRobotExploitWithSpread(
                selector.getByTicker(ticker),
                lots,
                Quotation.newBuilder().setUnits(minAskUnits).setNano(minAskNano).build(),
                Quotation.newBuilder().setUnits(maxBidUnits).setNano(maxBidNano).build()
        );
        exploitStrategy.post(command);
    }
}
