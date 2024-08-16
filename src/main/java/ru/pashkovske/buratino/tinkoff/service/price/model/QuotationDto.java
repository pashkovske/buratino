package ru.pashkovske.buratino.tinkoff.service.price.model;

public record QuotationDto(
        long units,
        int nanos
) {
    @Override
    public String toString() {
        return units + "." + nanos;
    }
}
