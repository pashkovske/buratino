package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.assignment.FollowPriceAssignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestSellPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.job.FollowBestPriceRefresher;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.contract.v1.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
public class FollowBestPrice implements OrderStrategy {
    private final OrderApi orderApi;
    private final MarketPriceService marketPriceService;
    //private final AssignmentDao currentAssignments;
    private final InstrumentSelector instrumentSelector;
    private final Map<UUID, Assignment> assignments = new HashMap<>();
    private final TaskScheduler taskScheduler;

    private final Duration DEFAULT_DELAY = Duration.ofSeconds(25);

    @Override
    public Assignment post(AssignmentCommand command) {
        UUID assignmentId = UUID.randomUUID();
        FollowPriceAssignment assignment = new FollowPriceAssignment(
                assignmentId,
                new OrderHolder(),
                command
        );
        assignments.put(assignmentId, assignment);
        MoneyValue price = PriceMapper.map(
                marketPriceService.getBestPrice(command.getInstrument(), command.getDirection()),
                command.getInstrument()
        );
        OrderRequest orderRequest = new OrderRequest(
                command.getInstrument().getId().id(),
                price,
                command.getDirection(),
                command.getLotsQuantity(),
                OrderType.ORDER_TYPE_LIMIT,
                TimeInForceType.TIME_IN_FORCE_DAY
        );
        assignment.getOrder().setOrderRequest(orderRequest);
        assignment.getOrder().setOrderResponse(orderApi.post(orderRequest));

        return assignment;
    }

    @Override
    public List<Assignment> pull(List<OrderState> orders) {
        for (OrderState order : orders) {
            if (order.getDirection().equals(OrderDirection.ORDER_DIRECTION_BUY)) {
                AssignmentCommand command = new FollowBestBuyPrice(
                        instrumentSelector.getById(new InstrumentId(order.getInstrumentUid())),
                        order.getLotsRequested() - order.getLotsExecuted()
                );
                orderApi.cancelOrder(order.getOrderId());
                post(command);
            } else if (order.getDirection().equals(OrderDirection.ORDER_DIRECTION_SELL)) {
                AssignmentCommand command = new FollowBestSellPrice(
                        instrumentSelector.getById(new InstrumentId(order.getInstrumentUid())),
                        order.getLotsRequested() - order.getLotsExecuted()
                );
                orderApi.cancelOrder(order.getOrderId());
                post(command);
            }
            else {
                throw new IllegalArgumentException("Найденная активная заявка с неизвестным направлением: " + order);
            }
        }
        return assignments.values().stream().toList();
    }

    @Override
    public List<Assignment> pullAndSchedule(List<OrderState> orders) {
        return pull(orders).stream()
                .peek(assignment -> scheduleRefresh(assignment.getId()))
                .toList();
    }

    @Override
    public void scheduleRefresh(UUID assignmentId) {
        Assignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Нет поручения с таким id");
        }
        FollowBestPriceRefresher follower = new FollowBestPriceRefresher(
                orderApi,
                marketPriceService,
                instrumentSelector,
                assignment
        );
        assignment.setScheduledFuture(taskScheduler.scheduleWithFixedDelay(follower, DEFAULT_DELAY));
    }

    @Override
    public Assignment refresh(UUID assignmentId) {
        Assignment assignment = assignments.get(assignmentId);
        if (assignment == null) {
            throw new IllegalArgumentException("Нет поручения с таким id");
        }
        List<OrderHolder> orders = assignment.getOrders();
        assert orders.size() == 1;
        OrderHolder order = orders.getFirst();
        String orderId = order.getOrderResponse().id();
        if (orderApi.getOrder(orderId).isActive()) {
            FollowBestPriceRefresher follower = new FollowBestPriceRefresher(
                    orderApi,
                    marketPriceService,
                    instrumentSelector,
                    assignment
            );
            follower.run();
            return assignment;
        }
        else {
            ScheduledFuture<?> scheduledFuture = assignment.getScheduledFuture();
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
            assignments.remove(assignmentId);
            return null;
        }
    }

    @Override
    public List<Assignment> refreshAll() {
        for (UUID assignmentId : assignments.keySet()) {
            refresh(assignmentId);
        }
        return assignments.values().stream().toList();
    }

    @Override
    public void cancel(UUID assignmentId) {
        Assignment assignment = assignments.get(assignmentId);
        List<OrderHolder> orders = assignment.getOrders();
        assert orders.size() == 1;
        OrderHolder order = orders.getFirst();
        String orderId = order.getOrderResponse().id();
        if (orderApi.getOrder(orderId).isActive()) {
            orderApi.cancelOrder(orderId);
        }
        ScheduledFuture<?> scheduledFuture = assignment.getScheduledFuture();
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
        assignments.remove(assignmentId);
    }

    @Override
    public List<Assignment> getAll() {
        return assignments.values().stream().toList();
    }
}
