package org.example.adapter.repository.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enterprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseEntity {
    @Id
    private UUID id;
    private String name;
    private String country;
    private Integer productionCapacity;
    private String timeZone;

    @OneToMany(mappedBy = "enterpriseEntity", cascade = CascadeType.ALL)
    private List<DriverEntity> driverEntities;

    @OneToMany(mappedBy = "enterpriseEntity", cascade = CascadeType.ALL)
    private List<VehicleEntity> vehicleEntities;

    @ManyToMany
    @JoinTable(
            name = "manager_enterprise",
            joinColumns = @JoinColumn(name = "enterprise_id"),
            inverseJoinColumns = @JoinColumn(name = "manager_id")
    )
    private List<ManagerEntity> managerEntities;
}
