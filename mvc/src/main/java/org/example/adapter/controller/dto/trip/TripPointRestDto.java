package org.example.adapter.controller.dto.trip;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripPointRestDto {
    private Double latitude;
    private Double longitude;
    private LocalDateTime date;
}
