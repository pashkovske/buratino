package ru.pashkovske.buratino.flow.exception

import ru.pashkovske.buratino.flow.readiness.NotReadyMessage

class FlowIsNotReadyException(
    messages: List<NotReadyMessage>
): BuildFlowException(
    message = messages.joinToString(
        separator = "\n\n---\n\n"
    )
)