package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.proposal;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.price.model.PriceType;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.Quotation;

public record Proposal(
        @NonNull Quotation price,
        @NonNull OrderDirection direction,
        @NonNull PriceType priceType,
        @NonNull ValidationState validationState
) {
}
