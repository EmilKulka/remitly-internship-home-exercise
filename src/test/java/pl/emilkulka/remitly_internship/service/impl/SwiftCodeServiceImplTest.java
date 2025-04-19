package pl.emilkulka.remitly_internship.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.emilkulka.remitly_internship.dto.*;
import pl.emilkulka.remitly_internship.exception.BadRequestException;
import pl.emilkulka.remitly_internship.exception.ResourceNotFoundException;
import pl.emilkulka.remitly_internship.mapper.SwiftCodeMapper;
import pl.emilkulka.remitly_internship.model.SwiftCode;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;
import pl.emilkulka.remitly_internship.util.SwiftCodeValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeServiceImplTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private SwiftCodeMapper swiftCodeMapper;

    @Mock
    private SwiftCodeValidator validator;

    @InjectMocks
    private SwiftCodeServiceImpl swiftCodeService;

    private SwiftCode headquarter;
    private SwiftCode branch;
    private SwiftCodeDTO swiftCodeDTO;
    private SwiftCodeDetailsDTO swiftCodeDetailsDTO;
    private SwiftCodeCreateDTO swiftCodeCreateDTO;
    private List<SwiftCode> countrySwiftCodes;
    private CountrySwiftCodesDTO countrySwiftCodesDTO;

    @BeforeEach
    void setUp() {
        headquarter = new SwiftCode();
        headquarter.setSwiftCode("ABCDEF12XXX");
        headquarter.setBankName("Test Bank");
        headquarter.setAddress("123 Test Street");
        headquarter.setCountryISO2("DE");
        headquarter.setCountryName("Germany");
        headquarter.setHeadquarterFlag(true);
        headquarter.setBranches(new ArrayList<>());

        branch = new SwiftCode();
        branch.setSwiftCode("ABCDEF12001");
        branch.setBankName("Test Bank Branch");
        branch.setAddress("456 Branch Street");
        branch.setCountryISO2("DE");
        branch.setCountryName("Germany");
        branch.setHeadquarterFlag(false);
        branch.setHeadquarter(headquarter);

        headquarter.getBranches().add(branch);

        swiftCodeDTO = new SwiftCodeDTO();
        swiftCodeDTO.setSwiftCode("ABCDEF12001");
        swiftCodeDTO.setBankName("Test Bank Branch");
        swiftCodeDTO.setAddress("456 Branch Street");
        swiftCodeDTO.setCountryISO2("DE");
        swiftCodeDTO.setCountryName("Germany");

        swiftCodeDetailsDTO = new SwiftCodeDetailsDTO();
        swiftCodeDetailsDTO.setSwiftCode("ABCDEF12XXX");
        swiftCodeDetailsDTO.setBankName("Test Bank");
        swiftCodeDetailsDTO.setAddress("123 Test Street");
        swiftCodeDetailsDTO.setCountryISO2("DE");
        swiftCodeDetailsDTO.setCountryName("Germany");

        swiftCodeCreateDTO = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDEF12XXX")
                .bankName("Test Bank")
                .address("123 Test Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(true)
                .build();

        countrySwiftCodes = new ArrayList<>();
        countrySwiftCodes.add(headquarter);
        countrySwiftCodes.add(branch);

        List<CountrySwiftCodeDTO> countrySwiftCodeDTOList = new ArrayList<>();
        CountrySwiftCodeDTO codeDTO1 = new CountrySwiftCodeDTO();
        codeDTO1.setSwiftCode("ABCDEF12XXX");
        codeDTO1.setBankName("Test Bank");
        CountrySwiftCodeDTO codeDTO2 = new CountrySwiftCodeDTO();
        codeDTO2.setSwiftCode("ABCDEF12001");
        codeDTO2.setBankName("Test Bank Branch");
        countrySwiftCodeDTOList.add(codeDTO1);
        countrySwiftCodeDTOList.add(codeDTO2);

        countrySwiftCodesDTO = CountrySwiftCodesDTO.builder()
                .countryISO2("DE")
                .countryName("Germany")
                .swiftCodes(countrySwiftCodeDTOList)
                .build();
    }

    @Test
    void getSwiftCodeDetails_WithExistingHeadquarterSwiftCode_ShouldReturnDetailedDTO() {
        // Given
        String swiftCode = "ABCDEF12XXX";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.of(headquarter));
        when(swiftCodeMapper.toSwiftCodeDetailsDTO(headquarter)).thenReturn(swiftCodeDetailsDTO);

        // When
        SwiftCodeDTO result = swiftCodeService.getSwiftCodeDetails(swiftCode);

        // Then
        assertThat(result).isEqualTo(swiftCodeDetailsDTO);
        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeMapper).toSwiftCodeDetailsDTO(headquarter);
    }

    @Test
    void getSwiftCodeDetails_WithExistingBranchSwiftCode_ShouldReturnBasicDTO() {
        // Given
        String swiftCode = "ABCDEF12001";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.of(branch));
        when(swiftCodeMapper.toSwiftCodeDTO(branch)).thenReturn(swiftCodeDTO);

        // When
        SwiftCodeDTO result = swiftCodeService.getSwiftCodeDetails(swiftCode);

        // Then
        assertThat(result).isEqualTo(swiftCodeDTO);
        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeMapper).toSwiftCodeDTO(branch);
    }

    @Test
    void getSwiftCodeDetails_WithNonExistingSwiftCode_ShouldThrowResourceNotFoundException() {
        // Given
        String swiftCode = "NONEXIST12XXX";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> swiftCodeService.getSwiftCodeDetails(swiftCode))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Swift code not found: " + swiftCode);

        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
    }

    @Test
    void getSwiftCodesByCountry_WithExistingCountry_ShouldReturnCountrySwiftCodesDTO() {
        // Given
        String countryISO2 = "DE";
        when(swiftCodeRepository.findByCountryISO2(countryISO2)).thenReturn(countrySwiftCodes);
        List<CountrySwiftCodeDTO> countrySwiftCodeDTOList = countrySwiftCodesDTO.getSwiftCodes();
        when(swiftCodeMapper.toCountrySwiftCodeDTOList(countrySwiftCodes)).thenReturn(countrySwiftCodeDTOList);

        // When
        CountrySwiftCodesDTO result = swiftCodeService.getSwiftCodesByCountry(countryISO2);

        // Then
        assertThat(result.getCountryISO2()).isEqualTo(countryISO2);
        assertThat(result.getCountryName()).isEqualTo("Germany");
        assertThat(result.getSwiftCodes()).isEqualTo(countrySwiftCodeDTOList);
        verify(validator).validateCountryISO(countryISO2);
        verify(swiftCodeRepository).findByCountryISO2(countryISO2);
        verify(swiftCodeMapper).toCountrySwiftCodeDTOList(countrySwiftCodes);
    }

    @Test
    void getSwiftCodesByCountry_WithNoSwiftCodesForCountry_ShouldThrowResourceNotFoundException() {
        // Given
        String countryISO2 = "US";
        when(swiftCodeRepository.findByCountryISO2(countryISO2)).thenReturn(Collections.emptyList());

        // When/Then
        assertThatThrownBy(() -> swiftCodeService.getSwiftCodesByCountry(countryISO2))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No swift codes found for country: " + countryISO2);

        verify(validator).validateCountryISO(countryISO2);
        verify(swiftCodeRepository).findByCountryISO2(countryISO2);
    }

    @Test
    void createSwiftCode_WithHeadquarter_ShouldSaveAndReturnSuccessMessage() {
        // Given
        when(swiftCodeMapper.fromCreateDto(swiftCodeCreateDTO)).thenReturn(headquarter);

        // When
        MessageResponseDTO result = swiftCodeService.createSwiftCode(swiftCodeCreateDTO);

        // Then
        assertThat(result.getMessage()).isEqualTo("Swift code created successfully");
        verify(validator).validateBusinessRules(swiftCodeCreateDTO);
        verify(swiftCodeMapper).fromCreateDto(swiftCodeCreateDTO);
        verify(swiftCodeRepository).save(headquarter);
        verifyNoMoreInteractions(swiftCodeRepository);
    }

    @Test
    void createSwiftCode_WithBranch_ShouldFindHeadquarterSaveAndReturnSuccessMessage() {
        // Given
        SwiftCodeCreateDTO branchCreateDTO = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDEF12001")
                .bankName("Test Bank Branch")
                .address("456 Branch Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(false)
                .build();

        SwiftCode branchEntity = new SwiftCode();
        branchEntity.setSwiftCode("ABCDEF12001");
        branchEntity.setHeadquarterFlag(false);

        when(swiftCodeMapper.fromCreateDto(branchCreateDTO)).thenReturn(branchEntity);
        when(swiftCodeRepository.findHeadquarterByBaseCode("ABCDEF12XXX")).thenReturn(Optional.of(headquarter));

        // When
        MessageResponseDTO result = swiftCodeService.createSwiftCode(branchCreateDTO);

        // Then
        assertThat(result.getMessage()).isEqualTo("Swift code created successfully");
        assertThat(branchEntity.getHeadquarter()).isEqualTo(headquarter);
        verify(validator).validateBusinessRules(branchCreateDTO);
        verify(swiftCodeMapper).fromCreateDto(branchCreateDTO);
        verify(swiftCodeRepository).findHeadquarterByBaseCode("ABCDEF12XXX");
        verify(swiftCodeRepository).save(branchEntity);
    }

    @Test
    void createSwiftCode_WithBranchAndNoHeadquarter_ShouldSaveWithoutHeadquarter() {
        // Given
        SwiftCodeCreateDTO branchCreateDTO = SwiftCodeCreateDTO.builder()
                .swiftCode("ABCDEF12001")
                .bankName("Test Bank Branch")
                .address("456 Branch Street")
                .countryISO2("DE")
                .countryName("Germany")
                .headquarter(false)
                .build();

        SwiftCode branchEntity = new SwiftCode();
        branchEntity.setSwiftCode("ABCDEF12001");
        branchEntity.setHeadquarterFlag(false);

        when(swiftCodeMapper.fromCreateDto(branchCreateDTO)).thenReturn(branchEntity);
        when(swiftCodeRepository.findHeadquarterByBaseCode("ABCDEF12XXX")).thenReturn(Optional.empty());

        // When
        MessageResponseDTO result = swiftCodeService.createSwiftCode(branchCreateDTO);

        // Then
        assertThat(result.getMessage()).isEqualTo("Swift code created successfully");
        assertThat(branchEntity.getHeadquarter()).isNull();
        verify(validator).validateBusinessRules(branchCreateDTO);
        verify(swiftCodeMapper).fromCreateDto(branchCreateDTO);
        verify(swiftCodeRepository).findHeadquarterByBaseCode("ABCDEF12XXX");
        verify(swiftCodeRepository).save(branchEntity);
    }

    @Test
    void deleteSwiftCode_WithExistingNonHeadquarterSwiftCode_ShouldDeleteAndReturnSuccessMessage() {
        // Given
        String swiftCode = "ABCDEF12001";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.of(branch));

        // When
        MessageResponseDTO result = swiftCodeService.deleteSwiftCode(swiftCode);

        // Then
        assertThat(result.getMessage()).isEqualTo("Swift code deleted successfully");
        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeRepository).deleteBySwiftCode(swiftCode);
    }

    @Test
    void deleteSwiftCode_WithHeadquarterWithNoBranches_ShouldDeleteAndReturnSuccessMessage() {
        // Given
        String swiftCode = "ABCDEF12XXX";
        SwiftCode headquarterWithoutBranches = new SwiftCode();
        headquarterWithoutBranches.setSwiftCode(swiftCode);
        headquarterWithoutBranches.setHeadquarterFlag(true);
        headquarterWithoutBranches.setBranches(new ArrayList<>());

        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.of(headquarterWithoutBranches));

        // When
        MessageResponseDTO result = swiftCodeService.deleteSwiftCode(swiftCode);

        // Then
        assertThat(result.getMessage()).isEqualTo("Swift code deleted successfully");
        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeRepository).deleteBySwiftCode(swiftCode);
    }

    @Test
    void deleteSwiftCode_WithHeadquarterWithBranches_ShouldThrowBadRequestException() {
        // Given
        String swiftCode = "ABCDEF12XXX";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.of(headquarter));

        // When/Then
        assertThatThrownBy(() -> swiftCodeService.deleteSwiftCode(swiftCode))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot delete headquarters with active branches");

        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeRepository, never()).deleteBySwiftCode(any());
    }

    @Test
    void deleteSwiftCode_WithNonExistingSwiftCode_ShouldThrowResourceNotFoundException() {
        // Given
        String swiftCode = "NONEXIST12XXX";
        when(swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> swiftCodeService.deleteSwiftCode(swiftCode))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Swift code not found: " + swiftCode);

        verify(validator).validateSwiftCode(swiftCode);
        verify(swiftCodeRepository).findBySwiftCodeWithBranches(swiftCode);
        verify(swiftCodeRepository, never()).deleteBySwiftCode(any());
    }
}