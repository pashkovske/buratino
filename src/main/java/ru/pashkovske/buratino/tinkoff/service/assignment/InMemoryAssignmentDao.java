package ru.pashkovske.buratino.tinkoff.service.assignment;

import lombok.NonNull;
import ru.pashkovske.buratino.tinkoff.service.instrument.model.InstrumentId;
import ru.tinkoff.piapi.contract.v1.OrderDirection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InMemoryAssignmentDao implements AssignmentDao {

    private final Map<UUID, Assignment> assignments = new HashMap<>();

    @Override
    public void post(@NonNull Assignment assignment) {
        if (assignments.containsKey(assignment.getId())) {
            throw new IllegalArgumentException("Попытка загрузить заявку с тем же id: " + assignment.getId().toString());
        }
        assignments.put(assignment.getId(), assignment);
    }

    @Override
    public void update(@NonNull UUID id, @NonNull Assignment assignment) {
        if (assignments.containsKey(assignment.getId())) {
            assignments.replace(assignment.getId(), assignment);
        }
        else {
            throw new NullPointerException("Попытка обновить несуществующую заявку");
        }
    }

    @Override
    public Assignment get(@NonNull UUID id) {
        return assignments.get(id);
    }

    @Override
    public List<Assignment> findByInstrument(@NonNull InstrumentId instrumentId) {
        return assignments.values().stream()
                .filter(assignment -> assignment.getCommand().getInstrument().getId().equals(instrumentId))
                .toList();
    }

    @Override
    public List<Assignment> findByInstrumentAndDirection(
            @NonNull InstrumentId instrumentId,
            @NonNull OrderDirection direction
    ) {
        return assignments.values().stream()
                .filter(assignment ->
                        assignment.getCommand().getInstrument().getId().equals(instrumentId)
                                && assignment.getCommand().getDirection().equals(direction))
                .toList();
    }

    @Override
    public void delete(@NonNull UUID id) {
        if (assignments.containsKey(id)) {
            assignments.remove(id);
        }
        else {
            throw new NullPointerException("Попытка удалить несуществующую заявку: " + id);
        }
    }
}
