package ru.pashkovske.buratino.tinkoff.service.assignment.strategy;

import ru.pashkovske.buratino.tinkoff.service.assignment.strategy.chain.ChainPrototype;

public interface Strategy {
    ChainPrototype getChainPrototype();

}
