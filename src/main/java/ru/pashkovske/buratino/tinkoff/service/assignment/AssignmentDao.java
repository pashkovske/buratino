package ru.pashkovske.buratino.tinkoff.service.assignment;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

import java.util.List;
import java.util.UUID;

public interface AssignmentDao {
    void post(@NonNull Assignment assignment);
    void update(@NonNull UUID id, @NonNull Assignment assignment);
    Assignment get(@NonNull UUID id);
    List<Assignment> findByInstrument(@NonNull InstrumentId instrumentId);
    List<Assignment> findByInstrumentAndDirection(@NonNull InstrumentId instrumentId, @NonNull OrderDirection direction);
    void delete(@NonNull UUID id);
}
