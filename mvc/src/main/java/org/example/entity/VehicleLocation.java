package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicle_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocation {

    @Id
    private UUID id;

    private Point location;

    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
}
