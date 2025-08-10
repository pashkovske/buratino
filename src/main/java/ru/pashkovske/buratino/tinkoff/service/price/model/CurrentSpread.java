package ru.pashkovske.buratino.tinkoff.service.price.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_spreads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentSpread {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "instrument_uid", nullable = false)
    private String instrumentUid;
    
    @Column(name = "min_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal minPrice;
    
    @Column(name = "max_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxPrice;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
