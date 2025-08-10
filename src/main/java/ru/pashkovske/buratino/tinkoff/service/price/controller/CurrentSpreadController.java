package ru.pashkovske.buratino.tinkoff.service.price.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pashkovske.buratino.tinkoff.service.price.dto.SpreadRequestDto;
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread;
import ru.pashkovske.buratino.tinkoff.service.price.service.CurrentSpreadService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/spreads")
@RequiredArgsConstructor
@Slf4j
public class CurrentSpreadController {
    
    private final CurrentSpreadService currentSpreadService;
    
    @PostMapping
    public ResponseEntity<CurrentSpread> saveSpread(@RequestBody SpreadRequestDto request) {
        
        log.info("Received request to save spread for instrument: {}", request.getInstrumentUid());
        CurrentSpread spread = currentSpreadService.saveSpread(
                request.getInstrumentUid(), 
                request.getMinPrice(), 
                request.getMaxPrice()
        );
        return ResponseEntity.ok(spread);
    }
    
    @GetMapping("/{instrumentUid}")
    public ResponseEntity<CurrentSpread> getSpreadByInstrumentUid(@PathVariable String instrumentUid) {
        log.info("Received request to get spread for instrument: {}", instrumentUid);
        return currentSpreadService.getSpreadByInstrumentUid(instrumentUid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<CurrentSpread>> getAllSpreads() {
        log.info("Received request to get all spreads");
        List<CurrentSpread> spreads = currentSpreadService.getAllSpreads();
        return ResponseEntity.ok(spreads);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CurrentSpread>> getSpreadsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        
        log.info("Received request to search spreads by price range: {} - {}", minPrice, maxPrice);
        List<CurrentSpread> spreads = currentSpreadService.getSpreadsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(spreads);
    }
    
    @DeleteMapping("/{instrumentUid}")
    public ResponseEntity<Void> deleteSpreadByInstrumentUid(@PathVariable String instrumentUid) {
        log.info("Received request to delete spread for instrument: {}", instrumentUid);
        currentSpreadService.deleteSpreadByInstrumentUid(instrumentUid);
        return ResponseEntity.noContent().build();
    }
}
