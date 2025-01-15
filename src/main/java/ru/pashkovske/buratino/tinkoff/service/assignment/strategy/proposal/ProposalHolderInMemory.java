package ru.pashkovske.buratino.tinkoff.service.assignment.strategy.proposal;

import lombok.Data;
import ru.pashkovske.buratino.tinkoff.service.price.model.PriceType;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.Map;

public class ProposalHolderInMemory implements ProposalHolder {


    @Data
    static private class ProposalValue {
        private final Quotation price;
        private ValidationState validationState;
    }
    @Data
    static private class AskBidProposalPair {
        private ProposalValue ask;
        private ProposalValue bid;
    }
}
