package ru.pashkovske.buratino.service.account;

import lombok.Getter;
import ru.tinkoff.piapi.core.UsersService;

public class TinkoffAccountResolver implements AccountResolver {
    @Getter
    final String brokerAccountId;

    public TinkoffAccountResolver(String name, UsersService usersService) {
        brokerAccountId = usersService.getAccountsSync().stream()
                .filter(account -> account.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getId();
    }
}
