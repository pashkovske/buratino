package ru.pashkovske.buratino.tinkoff.service.account;

import lombok.Getter;
import ru.tinkoff.piapi.core.UsersService;

public class AccountResolverImpl implements AccountResolver {
    @Getter
    final String brokerAccountId;

    public AccountResolverImpl(String name, UsersService usersService) {
        brokerAccountId = usersService.getAccountsSync().stream()
                .filter(account -> account.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getId();
    }
}
