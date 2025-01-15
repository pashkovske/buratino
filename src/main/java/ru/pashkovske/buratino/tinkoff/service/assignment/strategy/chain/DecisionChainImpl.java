package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.chain;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class DecisionChainImpl implements DecisionChain {
    private final List<Decision> chain;
}
