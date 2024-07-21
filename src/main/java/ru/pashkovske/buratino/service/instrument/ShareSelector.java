package ru.pashkovske.buratino.service.instrument;

import ru.tinkoff.piapi.contract.v1.Share;

public interface ShareSelector {
    Share findSingleByName(String name);
}
