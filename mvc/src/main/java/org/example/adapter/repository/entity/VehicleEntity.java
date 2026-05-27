package org.example.adapter.repository.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntity {
    @Id
    private UUID id;
    private Integer milleage;
    private BigDecimal price;
    private String country;
    private Instant productionDate;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private BrandEntity brandEntity;

    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private EnterpriseEntity enterpriseEntity;

    @OneToMany(mappedBy = "vehicleEntity", cascade = CascadeType.ALL)
    private List<DriverVehicleEntity> driverVehicleEntities;
}
