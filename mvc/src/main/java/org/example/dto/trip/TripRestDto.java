package org.example.dto.trip;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripRestDto {
    private UUID id;
    private Instant startDate;
    private Instant endDate;
    private TripPointRestDto startPoint;
    private String startAddress;
    private TripPointRestDto endPoint;
    private String endAddress;
}
