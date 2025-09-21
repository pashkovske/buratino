package ru.pashkovske.buratino.account.service

import ru.pashkovske.buratino.account.model.Account

interface AccountSupplier {
    fun findAccount(name: String): Account?
}