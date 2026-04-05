package ru.pashkovske.buratino.assignment.dao.notify.postgre

import org.springframework.context.annotation.DependsOn
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import ru.pashkovske.buratino.assignment.dao.notify.NotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper.PeriodicNotifierMapper
import ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc.PeriodicNotifierRepo
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicNotifierRow
import ru.pashkovske.buratino.assignment.exception.AssignmentNotifyException
import ru.pashkovske.buratino.assignment.model.notify.AssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.PeriodicAssignmentNotifier
import ru.pashkovske.buratino.assignment.model.notify.NotifierState
import java.util.UUID

@DependsOn("flywayInitializer")
abstract class BasicPostgreNotifierDao<Row : PeriodicNotifierRow>(
    private val repository: PeriodicNotifierRepo<Row>,
    private val mapper: PeriodicNotifierMapper<Row>,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) : NotifierDao {

    override fun get(id: UUID): AssignmentNotifier {
        return find(id) ?: throw AssignmentNotifyException(
            message = "Continue notifier not found in PostgreSQL",
            notifierId = id,
            assignmentId = null
        )
    }

    override fun find(id: UUID): AssignmentNotifier? {
        return repository.findById(id)
            .map(mapper::toNotifier)
            .block()
    }

    override fun findByAssignmentId(assignmentId: UUID): List<AssignmentNotifier> {
        return repository.findByAssignmentId(assignmentId)
            .map(mapper::toNotifier)
            .collectList()
            .block() ?: emptyList()
    }

    override fun findByState(state: NotifierState): List<AssignmentNotifier> {
        return repository.findByState(state.toString())
            .map(mapper::toNotifier)
            .collectList()
            .block() ?: emptyList()
    }

    override fun create(notifier: AssignmentNotifier) {
        when (notifier) {
            is PeriodicAssignmentNotifier ->
                r2dbcEntityTemplate.insert(mapper.toRow(notifier))
                    .block()
        }
    }

    override fun update(notifier: AssignmentNotifier) {
        when (notifier) {
            is PeriodicAssignmentNotifier ->
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
