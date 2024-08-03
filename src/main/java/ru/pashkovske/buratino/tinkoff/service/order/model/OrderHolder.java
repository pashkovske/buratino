package ru.pashkovske.buratino.tinkoff.service.order.model;

import lombok.Data;

@Data
public class OrderHolder {
    OrderRequest orderRequest;
    OrderResponse orderResponse;
}
