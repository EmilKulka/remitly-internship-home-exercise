package pl.emilkulka.remitly_internship.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.emilkulka.remitly_internship.dto.SwiftCodeCreateDTO;
import pl.emilkulka.remitly_internship.exception.BadRequestException;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwiftCodeValidatorTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @InjectMocks
    private SwiftCodeValidator validator;

    private SwiftCodeCreateDTO validHeadquarterDto;
    private SwiftCodeCreateDTO validBranchDto;

    @BeforeEach
    void setUp() {
        validHeadquarterDto = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDDE12XXX")
                .bankName("Test Bank")
                .address("123 Test Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(true)
                .build();

        validBranchDto = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDDE12001")
                .bankName("Test Bank Branch")
                .address("456 Branch Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(false)
                .build();
    }

    @Test
    void isValidSwiftCodeFormat_WithValidFormat_ShouldReturnTrue() {
        // When/Then
        assertThat(validator.isValidSwiftCodeFormat("ABCDEF12XXX")).isTrue();
        assertThat(validator.isValidSwiftCodeFormat("ABCDEF12")).isTrue();
        assertThat(validator.isValidSwiftCodeFormat("ABCDEF12123")).isTrue();
    }

    @Test
    void isValidSwiftCodeFormat_WithInvalidFormat_ShouldReturnFalse() {
        // When/Then
        assertThat(validator.isValidSwiftCodeFormat("invalid")).isFalse();
        assertThat(validator.isValidSwiftCodeFormat("ABCDE12")).isFalse();
        assertThat(validator.isValidSwiftCodeFormat("abcdef12")).isFalse();
        assertThat(validator.isValidSwiftCodeFormat(null)).isFalse();
    }

    @Test
    void isValidCountryISOFormat_WithValidFormat_ShouldReturnTrue() {
        // When/Then
        assertThat(validator.isValidCountryISOFormat("US")).isTrue();
        assertThat(validator.isValidCountryISOFormat("DE")).isTrue();
    }

    @Test
    void isValidCountryISOFormat_WithInvalidFormat_ShouldReturnFalse() {
        // When/Then
        assertThat(validator.isValidCountryISOFormat("USA")).isFalse();
        assertThat(validator.isValidCountryISOFormat("us")).isFalse();
        assertThat(validator.isValidCountryISOFormat("1")).isFalse();
        assertThat(validator.isValidCountryISOFormat(null)).isFalse();
    }

    @Test
    void validateSwiftCode_WithValidCode_ShouldNotThrowException() {
        // When/Then
        validator.validateSwiftCode("ABCDEF12XXX");
    }

    @Test
    void validateSwiftCode_WithInvalidCode_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> validator.validateSwiftCode("invalid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid swift code format");
    }

    @Test
    void validateCountryISO_WithValidISO_ShouldNotThrowException() {
        // When/Then
        validator.validateCountryISO("US");
    }

    @Test
    void validateCountryISO_WithInvalidISO_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> validator.validateCountryISO("invalid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid country ISO format");
    }

    @Test
    void validateBusinessRules_WithValidHeadquarter_ShouldNotThrowException() {
        // Given
        when(swiftCodeRepository.existsBySwiftCode("ABCDDE12XXX")).thenReturn(false);

        // When/Then
        validator.validateBusinessRules(validHeadquarterDto);
    }

    @Test
    void validateBusinessRules_WithValidBranch_ShouldNotThrowException() {
        // Given
        when(swiftCodeRepository.existsBySwiftCode("ABCDDE12001")).thenReturn(false);

        // When/Then
        validator.validateBusinessRules(validBranchDto);
    }

    @Test
    void validateBusinessRules_WithDuplicateSwiftCode_ShouldThrowException() {
        // Given
        when(swiftCodeRepository.existsBySwiftCode("ABCDDE12XXX")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> validator.validateBusinessRules(validHeadquarterDto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void validateBusinessRules_WithInvalidHeadquarterFormat_ShouldThrowException() {
        // Given
        SwiftCodeCreateDTO invalidHeadquarterDto = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDEF12123")
                .bankName("Test Bank")
                .address("123 Test Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(true)
                .build();

        when(swiftCodeRepository.existsBySwiftCode("ABCDEF12123")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> validator.validateBusinessRules(invalidHeadquarterDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Headquarters swift code must end with 'XXX'");
    }

    @Test
    void validateBusinessRules_WithInvalidBranchFormat_ShouldThrowException() {
        // Given
        SwiftCodeCreateDTO invalidBranchDto = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDEF12XXX")
                .bankName("Test Bank Branch")
                .address("456 Branch Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(false)
                .build();

        when(swiftCodeRepository.existsBySwiftCode("ABCDEF12XXX")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> validator.validateBusinessRules(invalidBranchDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Branch swift code cannot end with 'XXX'");
    }

    @Test
    void validateBusinessRules_WithInconsistentCountry_ShouldThrowException() {
        // Given
        SwiftCodeCreateDTO inconsistentCountryDto = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDUS12XXX")
                .bankName("Test Bank")
                .address("123 Test Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(true)
                .build();

        when(swiftCodeRepository.existsBySwiftCode("ABCDUS12XXX")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> validator.validateBusinessRules(inconsistentCountryDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Country ISO code (DE) does not match SWIFT code country identifier (US)");
    }


}