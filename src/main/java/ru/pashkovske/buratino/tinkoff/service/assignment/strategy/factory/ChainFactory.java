package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.factory;

import ru.pashkovske.buratino.tinkoff.service.assignment.strategy.chain.DecisionChain;

public interface ChainFactory {
    public DecisionChain buildDefault();
}
