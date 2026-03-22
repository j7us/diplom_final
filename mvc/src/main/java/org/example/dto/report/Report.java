package org.example.dto.report;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String name;
    private String intervalType;
    private Instant dateFrom;
    private Instant dateTo;
    private Map<String, BigDecimal> result;
}
