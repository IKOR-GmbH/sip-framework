package ${package}.scenarios.models;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class GeoCodingResponse {
    BigDecimal generationtime_ms;
    List<GeoCodingResult> results;
}