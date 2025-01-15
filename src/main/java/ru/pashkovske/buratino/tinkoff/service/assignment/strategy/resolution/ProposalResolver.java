package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.resolution;

import ru.pashkovske.buratino.tinkoff.service.assignment.strategy.proposal.ProposalHolder;

public interface ProposalResolver {
    FinalResolution resolve(ProposalHolder proposalHolder);
}
