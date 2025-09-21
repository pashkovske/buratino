package ru.pashkovske.buratino.account.adapter.tinkoff

import ru.pashkovske.buratino.account.model.Account

object TinkoffAccountMapper {
    fun map(tinkoffAccount: ru.tinkoff.piapi.contract.v1.Account): Account {
        return Account(
            name = tinkoffAccount.name,
            id = tinkoffAccount.id
        )
    }
}