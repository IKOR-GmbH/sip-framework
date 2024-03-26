package ${package}.scenarios.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirQualityResponse {

    String requestedBy = "";
    BigDecimal generationtime_ms;
    Integer utc_offset_seconds;
    String timezone;
    String timezone_abbreviation;

    AirQualityHourlyUnits hourly_units;

    AirQualityHourlyData hourly;
}