package ${package}.scenarios.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class GeoCodingResult {
    Integer id;
    String name;
    BigDecimal latitude;
    BigDecimal longitude;
    Integer elevation;
    String feature_code;
    String country_code;
    Integer admin1_id;
    Integer admin2_id;
    Integer admin3_id;
    Integer admin4_id;
    String timezone;
    Integer population;
    Integer country_id;
    List postcodes;
    String country;
    String admin1;
    String admin2;
    String admin3;
    String admin4;
}
