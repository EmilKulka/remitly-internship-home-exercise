package pl.emilkulka.remitly_internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeBranchDTO {
    private String swiftCode;
    private String bankName;
    private String address;
    private String countryISO2;
    private boolean isHeadquarter;
}