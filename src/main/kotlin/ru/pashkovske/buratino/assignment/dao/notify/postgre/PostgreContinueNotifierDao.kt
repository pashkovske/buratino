package ru.pashkovske.buratino.assignment.dao.notify.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.notify.ContinueNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper.PeriodicContinueNotifierMapper
import ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc.PeriodicContinueNotifierRepo
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicContinueNotifierRow

@Repository
class PostgreContinueNotifierDao(
    repository: PeriodicContinueNotifierRepo,
    mapper: PeriodicContinueNotifierMapper,
    r2dbcEntityTemplate: R2dbcEntityTemplate
) : BasicPostgreNotifierDao<PeriodicContinueNotifierRow>(
    repository = repository,
    mapper = mapper,
    r2dbcEntityTemplate = r2dbcEntityTemplate
), ContinueNotifierDao
