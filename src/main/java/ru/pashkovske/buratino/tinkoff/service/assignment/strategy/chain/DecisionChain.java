package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.chain;

import java.util.List;

public interface DecisionChain {
    public List<Decision> getChain();
}
