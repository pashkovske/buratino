package ru.pashkovske.buratino.tinkoff.service.account;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class TinkoffCustomerConfig {
    @Value("${buratino.access.token.env.var}")
    String tinkoffAccessToken;
}
