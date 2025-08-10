package ru.pashkovske.buratino.tinkoff.service.price.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pashkovske.buratino.tinkoff.service.price.model.CurrentSpread;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrentSpreadRepository extends JpaRepository<CurrentSpread, Long> {
    
    Optional<CurrentSpread> findByInstrumentUid(String instrumentUid);
    
    List<CurrentSpread> findByInstrumentUidIn(List<String> instrumentUids);
    
    @Query("SELECT cs FROM CurrentSpread cs WHERE cs.minPrice >= :minPrice AND cs.maxPrice <= :maxPrice")
    List<CurrentSpread> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                       @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    boolean existsByInstrumentUid(String instrumentUid);
}
