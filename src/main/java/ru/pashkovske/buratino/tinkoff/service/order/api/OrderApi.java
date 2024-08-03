package ru.pashkovske.buratino.tinkoff.service.order.api;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderRequest;
import ru.pashkovske.buratino.tinkoff.service.order.model.OrderResponse;

public interface OrderApi {
    @NonNull OrderResponse post(@NonNull OrderRequest order);
    @NonNull OrderResponse replaceOrder(@NonNull String orderId, @NonNull OrderRequest order);
    void cancelOrder(@NonNull String orderId);
}
