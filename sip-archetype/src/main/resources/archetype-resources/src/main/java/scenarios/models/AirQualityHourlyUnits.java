package ${package}.scenarios.models;

import lombok.Data;

@Data
public class AirQualityHourlyUnits {
    String time;
    String pm10;
    String pm2_5;
}