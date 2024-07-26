package ru.pashkovske.buratino.tinkoff.service.instrument;

import ru.tinkoff.piapi.contract.v1.Future;

import java.util.List;

public interface FutureSelector {
    Future getByTicker(String ticker);
    List<Future> findByName(String name);
}
