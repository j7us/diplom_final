package org.example.entity;

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
public class Enterprise {
    @Id
    private UUID id;
    private String name;
    private String country;
    private Integer productionCapacity;

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private List<Driver> drivers;

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private List<Vehicle> vehicles;

    @ManyToMany
    @JoinTable(
            name = "manager_enterprise",
            joinColumns = @JoinColumn(name = "enterprise_id"),
            inverseJoinColumns = @JoinColumn(name = "manager_id")
    )
    private List<Manager> managers;
}
