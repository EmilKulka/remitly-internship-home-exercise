package pl.emilkulka.remitly_internship.service;

import pl.emilkulka.remitly_internship.dto.*;

public interface SwiftCodeService {
    /**
     * Get details of a SWIFT code including branches if it's a headquarter
     * @param swiftCode the SWIFT code to retrieve
     * @return SwiftCodeDetailsDTO with branches if it's a headquarter
     */
    SwiftCodeDTO getSwiftCodeDetails(String swiftCode);

    /**
     * Get all SWIFT codes for a specific country
     * @param countryISO2 ISO-2 country code
     * @return CountrySwiftCodesDTO containing all SWIFT codes for the country
     */
    CountrySwiftCodesDTO getSwiftCodesByCountry(String countryISO2);

    /**
     * Create a new SWIFT code entry
     * @param createDto the SWIFT code data to create
     * @return MessageResponseDTO with success message
     */
    MessageResponseDTO createSwiftCode(SwiftCodeCreateDTO createDto);

    /**
     * Delete a SWIFT code entry
     * @param swiftCode the SWIFT code to delete
     * @return MessageResponseDTO with success message
     */
    MessageResponseDTO deleteSwiftCode(String swiftCode);
}