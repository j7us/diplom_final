package org.example.dto.vehiclelocation;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleLocationJsonRestDto {
    private UUID id;
    private LocalDateTime date;
    private Double latitude;
    private Double longitude;
}
