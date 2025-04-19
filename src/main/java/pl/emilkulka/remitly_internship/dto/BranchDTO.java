package pl.emilkulka.remitly_internship.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "swiftCode",
        "bankName",
        "address",
        "countryISO2",
        "isHeadquarter"
})
public class BranchDTO {
    private String swiftCode;
    private String bankName;
    private String address;
    private String countryISO2;
    @JsonProperty("isHeadquarter")
    private boolean isHeadquarter;
}
