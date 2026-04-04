package ru.pashkovske.buratino.assignment.dao.notify.postgre.r2dbc

import ru.pashkovske.buratino.assignment.dao.notify.postgre.row.PeriodicRefreshNotifierRow

interface PeriodicRefreshNotifierRepo : PeriodicNotifierRepo<PeriodicRefreshNotifierRow>
