package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enterprises")
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

    @OneToMany(mappedBy = "enterprise")
    private List<Driver> drivers;

    @OneToMany(mappedBy = "enterprise")
    private List<Vehicle> vehicles;

    @ManyToMany(mappedBy = "enterprises")
    private List<Manager> managers;
}
