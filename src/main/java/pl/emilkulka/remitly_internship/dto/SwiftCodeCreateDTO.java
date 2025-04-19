package pl.emilkulka.remitly_internship.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeCreateDTO {

    @NotBlank(message = "Swift code cannot be empty")
    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid swift code format")
    private String swiftCode;
    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;
    private String address;
    @NotBlank(message = "Country ISO code cannot be empty")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid country ISO format")
    private String countryISO2;
    @NotBlank(message = "Country name cannot be empty")
    private String countryName;
    @JsonProperty("isHeadquarter")
    private boolean headquarter;
}