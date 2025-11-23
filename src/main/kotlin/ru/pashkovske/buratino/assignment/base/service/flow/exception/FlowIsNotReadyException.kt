package ru.pashkovske.buratino.assignment.base.service.flow.exception

import ru.pashkovske.buratino.assignment.base.service.flow.readiness.NotReadyMessage

class FlowIsNotReadyException(
    messages: List<NotReadyMessage>
): BuildFlowException(
    message = messages.joinToString(
        separator = "\n\n---\n\n"
    )
)