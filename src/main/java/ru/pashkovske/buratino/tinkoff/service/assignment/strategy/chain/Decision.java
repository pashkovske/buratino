package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.chain;

import lombok.NonNull;

import ru.pashkovske.buratino.tinkoff.service.assignment.strategy.proposal.ProposalHolder;
import ru.pashkovske.buratino.tinkoff.service.instrument.context.InstrumentCtx;

public interface Decision {
    void doDecision(
            @NonNull ProposalHolder proposalHolder,
            @NonNull InstrumentCtx instrumentCtx
    );
}