package ru.pashkovske.buratino.assignment.dao.notify.postgre

import org.springframework.context.annotation.DependsOn
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper.PeriodicNotifierMapper
import ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc.PeriodicNotifierRepo
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicNotifierRow
import ru.pashkovske.buratino.assignment.exception.AssignmentNotifyException
import ru.pashkovske.buratino.assignment.model.notify.AssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentScheduling
import ru.pashkovske.buratino.assignment.model.notify.SchedulingState
import java.util.UUID

@DependsOn("flywayInitializer")
abstract class BasicPostgreNotifierDao<Row : PeriodicNotifierRow>(
    private val repository: PeriodicNotifierRepo<Row>,
    private val mapper: PeriodicNotifierMapper<Row>,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : NotifierDao {

    override fun get(id: UUID): AssignmentScheduling {
        return find(id) ?: throw AssignmentNotifyException(
            message = "Continue notifier not found in PostgreSQL",
            notifierId = id,
            assignmentId = null
        )
    }

    override fun find(id: UUID): AssignmentScheduling? {
        return repository.findById(id)
            .map(mapper::toNotifier)
            .block()
    }

    override fun findByAssignmentId(assignmentId: UUID): List<AssignmentScheduling> {
        return repository.findByAssignmentId(assignmentId)
            .map(mapper::toNotifier)
            .collectList()
            .block() ?: emptyList()
    }

    override fun findByState(state: SchedulingState): List<AssignmentScheduling> {
        return repository.findByState(state.toString())
            .map(mapper::toNotifier)
            .collectList()
            .block() ?: emptyList()
    }

    override fun create(notifier: AssignmentScheduling) {
        when (notifier) {
            is PeriodicAssignmentScheduling ->
                r2dbcEntityTemplate.insert(mapper.toRow(notifier))
                    .block()
        }
    }

    override fun update(notifier: AssignmentScheduling) {
        when (notifier) {
            is PeriodicAssignmentScheduling ->
                r2dbcEntityTemplate.update(mapper.toRow(notifier))
                    .block()
        }
    }

    override fun delete(id: UUID) {
        repository.deleteById(id)
            .block()
    }

    override fun deleteAll() {
        repository.deleteAll()
            .block()
    }
}
