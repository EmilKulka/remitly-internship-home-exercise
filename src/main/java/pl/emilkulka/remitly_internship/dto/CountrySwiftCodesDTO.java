package pl.emilkulka.remitly_internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountrySwiftCodesDTO {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDTO> swiftCodes;
}