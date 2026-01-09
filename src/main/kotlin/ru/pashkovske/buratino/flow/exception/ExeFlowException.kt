package ru.pashkovske.buratino.flow.exception

class ExeFlowException(
    message: String,
    name: String,
    nodeName: String,
    nodeType: String
): RuntimeException(
    """
        Flow execution error: $message
        Flow name: $name
        Node name: $nodeName
        Node type: $nodeType
    """.trimIndent()
)