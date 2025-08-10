package ru.pashkovske.buratino.tinkoff.service.price.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread;
import ru.pashkovske.buratino.tinkoff.service.price.repository.CurrentSpreadRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentSpreadService {
    
    private final CurrentSpreadRepository currentSpreadRepository;
    
    @Transactional
    public CurrentSpread saveSpread(String instrumentUid, BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Saving spread for instrument {}: min={}, max={}", instrumentUid, minPrice, maxPrice);
        
        Optional<CurrentSpread> existingSpread = currentSpreadRepository.findByInstrumentUid(instrumentUid);
        
        if (existingSpread.isPresent()) {
            CurrentSpread spread = existingSpread.get();
            spread.setMinPrice(minPrice);
            spread.setMaxPrice(maxPrice);
            return currentSpreadRepository.save(spread);
        } else {
            CurrentSpread newSpread = new CurrentSpread();
            newSpread.setInstrumentUid(instrumentUid);
            newSpread.setMinPrice(minPrice);
            newSpread.setMaxPrice(maxPrice);
            return currentSpreadRepository.save(newSpread);
        }
    }
    
    @Transactional(readOnly = true)
    public Optional<CurrentSpread> getSpreadByInstrumentUid(String instrumentUid) {
        return currentSpreadRepository.findByInstrumentUid(instrumentUid);
    }
    
    @Transactional(readOnly = true)
    public List<CurrentSpread> getAllSpreads() {
        return currentSpreadRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<CurrentSpread> getSpreadsByInstrumentUids(List<String> instrumentUids) {
        return currentSpreadRepository.findByInstrumentUidIn(instrumentUids);
    }
    
    @Transactional(readOnly = true)
    public List<CurrentSpread> getSpreadsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return currentSpreadRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Transactional
    public void deleteSpreadByInstrumentUid(String instrumentUid) {
        currentSpreadRepository.findByInstrumentUid(instrumentUid)
                .ifPresent(currentSpreadRepository::delete);
    }
}
