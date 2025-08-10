package ru.pashkovske.buratino.tinkoff.service.price.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpreadRequestDto {
    private String instrumentUid;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
