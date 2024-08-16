package ru.pashkovske.buratino.tinkoff.service.price.model;

public record QuotationDto(
        long units,
        int nanos
) {
    @Override
    public String toString() {
        return Long.toString(units) + "." + Integer.toString(nanos);
    }
}
