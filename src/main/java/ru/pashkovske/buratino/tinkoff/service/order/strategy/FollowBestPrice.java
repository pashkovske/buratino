package ru.pashkovske.buratino.tinkoff.service.order.strategy;

import lombok.RequiredArgsConstructor;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.pashkovske.buratino.tinkoff.service.instrument.selector.InstrumentSelector;
import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderHolder;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.AssignmentDao;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.assignment.FollowPriceAssignment;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.AssignmentCommand;
import ru.pashkovske.buratino.tinkoff.service.order.api.OrderApi;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestBuyPrice;
import ru.pashkovske.buratino.tinkoff.service.order.strategy.command.FollowBestSellPrice;
import ru.pashkovske.buratino.tinkoff.service.price.mapper.PriceMapper;
import ru.pashkovske.buratino.tinkoff.service.price.service.MarketPriceService;
import ru.tinkoff.piapi.contract.v1.*;

import java.util.*;

@RequiredArgsConstructor
public class FollowBestPrice implements OrderStrategy {
    private final OrderApi orderApi;
    private final MarketPriceService marketPriceService;
    //private final AssignmentDao currentAssignments;
    private final InstrumentSelector instrumentSelector;
    private final Map<UUID, Assignment> assignments = new HashMap<>();

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
                throw new IllegalArgumentException("Найденная активная заявка с неспецифицированным направлением: " + order);
            }
        }
        return assignments.values().stream().toList();
    }

    @Override
    public Assignment refresh(UUID assignmentId) {
        Assignment assignment = assignments.get(assignmentId);
        AssignmentCommand command = assignment.getCommand();

        MoneyValue price = PriceMapper.map(
                marketPriceService.getBestPrice(
                        command.getInstrument(),
                        assignment.getOrders().stream().map(OrderDataMapper::map).toList(),
                        command.getDirection()
                ),
                command.getInstrument()
        );

        List<OrderHolder> orders = assignment.getOrders();
        assert orders.size() == 1;
        OrderHolder order = orders.getFirst();
        if (!order.getOrderRequest().price().equals(price)) {
            String orderId = order.getOrderResponse().id();

            OrderRequest orderRequest = new OrderRequest(
                    command.getInstrument().getId().id(),
                    price,
                    command.getDirection(),
                    command.getLotsQuantity(),
                    OrderType.ORDER_TYPE_LIMIT,
                    TimeInForceType.TIME_IN_FORCE_DAY
            );

            order.setOrderRequest(orderRequest);
            order.setOrderResponse(orderApi.replaceOrder(orderId, orderRequest));
        }
        return assignment;
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
        orderApi.cancelOrder(orderId);
        assignments.remove(assignmentId);
    }
}
