package ru.pashkovske.buratino.account.adapter.tinkoff

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.account.model.Account
import ru.pashkovske.buratino.account.service.AccountSupplier
import ru.tinkoff.piapi.core.UsersService

@Service
class TinkoffAccountSupplierApi(
    private val tinkoffUserService: UsersService
): AccountSupplier {
    private val accountMapper = TinkoffAccountMapper

    override fun findAccount(name: String): Account? {
        return tinkoffUserService.accountsSync
            .map(accountMapper::map)
            .find { it.name == name }
    }
}