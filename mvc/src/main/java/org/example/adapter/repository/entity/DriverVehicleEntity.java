package org.example.adapter.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver_vehicle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverVehicleEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private DriverEntity driverEntity;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicleEntity;

    private Boolean active;
}
