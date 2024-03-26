package ${package}.scenarios.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeoCodingRequest {
    private String cityName;
}