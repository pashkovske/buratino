package ru.pashkovske.buratino.assignment.dao.notify.postgre

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.pashkovske.buratino.assignment.dao.notify.RefreshNotifierDao
import ru.pashkovske.buratino.assignment.dao.notify.postgre.mapper.PeriodicRefreshNotifierMapper
import ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc.PeriodicRefreshNotifierRepo
import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicRefreshNotifierRow

@Repository
class PostgreRefreshNotifierDao(
    repository: PeriodicRefreshNotifierRepo,
    mapper: PeriodicRefreshNotifierMapper,
    r2dbcEntityTemplate: R2dbcEntityTemplate
) : BasicPostgreNotifierDao<PeriodicRefreshNotifierRow>(
    repository = repository,
    mapper = mapper,
    r2dbcEntityTemplate = r2dbcEntityTemplate
), RefreshNotifierDao
