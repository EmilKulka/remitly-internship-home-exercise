package pl.emilkulka.remitly_internship.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "countryISO2",
        "countryName",
        "swiftCodes"
})
public class CountrySwiftCodesDTO {
    private String countryISO2;
    private String countryName;
    private List<CountrySwiftCodeDTO> swiftCodes;
}