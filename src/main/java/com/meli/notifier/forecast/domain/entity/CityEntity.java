package com.meli.notifier.forecast.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityEntity {

    @Id
    @Column(name = "id_cptec")
    private Long idCptec;

    @Column(nullable = false)
    private String name;

    @Column(name = "state_code", nullable = false, length = 2)
    private String stateCode;

    @Column(name = "is_coastal")
    private Boolean isCoastal;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
