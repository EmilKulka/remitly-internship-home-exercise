package pl.emilkulka.remitly_internship.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.emilkulka.remitly_internship.dto.SwiftCodeCreateDTO;
import pl.emilkulka.remitly_internship.exception.BadRequestException;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SwiftCodeValidator {

    private static final Pattern SWIFT_CODE_PATTERN = Pattern.compile("^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    private static final Pattern ISO_COUNTRY_PATTERN = Pattern.compile("^[A-Za-z]{2}$");

    private final SwiftCodeRepository swiftCodeRepository;

    public boolean isValidSwiftCodeFormat(String swiftCode) {
        return swiftCode != null && SWIFT_CODE_PATTERN.matcher(swiftCode).matches();
    }

    public boolean isValidCountryISOFormat(String countryISO2) {
        return countryISO2 != null && ISO_COUNTRY_PATTERN.matcher(countryISO2).matches();
    }

    public void validateSwiftCode(String swiftCode) {
        if (!StringUtils.hasText(swiftCode)) {
            throw new BadRequestException("Swift code cannot be empty");
        }

        if (!isValidSwiftCodeFormat(swiftCode)) {
            throw new BadRequestException("Invalid swift code format: " + swiftCode);
        }
    }

    public void validateCountryISO(String countryISO2) {
        if (!StringUtils.hasText(countryISO2)) {
            throw new BadRequestException("Country ISO code cannot be empty");
        }

        if (!isValidCountryISOFormat(countryISO2)) {
            throw new BadRequestException("Invalid country ISO format: " + countryISO2);
        }
    }

    public void validateBusinessRules(SwiftCodeCreateDTO dto) {

        if (swiftCodeRepository.existsBySwiftCode(dto.getSwiftCode())) {
            throw new BadRequestException("Swift code already exists: " + dto.getSwiftCode());
        }

        if (dto.isHeadquarter() && !dto.getSwiftCode().endsWith("XXX")) {
            throw new BadRequestException("Headquarters swift code must end with 'XXX'");
        }

        if (!dto.isHeadquarter() && dto.getSwiftCode().endsWith("XXX")) {
            throw new BadRequestException("Branch swift code cannot end with 'XXX'");
        }

        int length = dto.getSwiftCode().length();

        if (length != 8 && length != 11) {
            throw new BadRequestException("Swift code must be either 8 or 11 characters long");
        }

        validateCountryConsistency(dto);
    }

    private void validateCountryConsistency(SwiftCodeCreateDTO dto) {
        String countryCodeFromSwift = dto.getSwiftCode().substring(4, 6);
        if (!countryCodeFromSwift.equals(dto.getCountryISO2())) {
            throw new BadRequestException("Country ISO code (" + dto.getCountryISO2() +
                    ") does not match SWIFT code country identifier (" + countryCodeFromSwift + ")");
        }
    }
}