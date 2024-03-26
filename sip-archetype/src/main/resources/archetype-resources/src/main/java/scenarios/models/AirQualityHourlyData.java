package ${package}.scenarios.models;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AirQualityHourlyData {
    List<String> time;
    List<BigDecimal> pm10;
    List<BigDecimal> pm2_5;
}
