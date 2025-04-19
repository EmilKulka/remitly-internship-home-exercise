package pl.emilkulka.remitly_internship.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.emilkulka.remitly_internship.dto.*;
import pl.emilkulka.remitly_internship.exception.BadRequestException;
import pl.emilkulka.remitly_internship.exception.ResourceNotFoundException;
import pl.emilkulka.remitly_internship.mapper.SwiftCodeMapper;
import pl.emilkulka.remitly_internship.model.SwiftCode;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;
import pl.emilkulka.remitly_internship.service.SwiftCodeService;
import pl.emilkulka.remitly_internship.util.SwiftCodeValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SwiftCodeServiceImpl implements SwiftCodeService {

    private final SwiftCodeRepository swiftCodeRepository;
    private final SwiftCodeMapper swiftCodeMapper;
    private final SwiftCodeValidator validator;

    @Override
    public SwiftCodeDTO getSwiftCodeDetails(String swiftCode) {
        validator.validateSwiftCode(swiftCode);

        SwiftCode swiftCodeEntity = swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)
                .orElseThrow(() -> new ResourceNotFoundException("Swift code not found: " + swiftCode));

        return swiftCodeEntity.isHeadquarterFlag()
                ? swiftCodeMapper.toSwiftCodeDetailsDTO(swiftCodeEntity)
                : swiftCodeMapper.toSwiftCodeDTO(swiftCodeEntity);
    }

    @Override
    public CountrySwiftCodesDTO getSwiftCodesByCountry(String countryISO2) {
        validator.validateCountryISO(countryISO2);

        List<SwiftCode> swiftCodes = swiftCodeRepository.findByCountryISO2(countryISO2);

        if (swiftCodes.isEmpty()) {
            throw new ResourceNotFoundException("No swift codes found for country: " + countryISO2);
        }

        List<CountrySwiftCodeDTO> countrySwiftCodeDTOList = swiftCodeMapper.toCountrySwiftCodeDTOList(swiftCodes);

        return CountrySwiftCodesDTO.builder()
                .countryISO2(countryISO2)
                .countryName(swiftCodes.getFirst().getCountryName())
                .swiftCodes(countrySwiftCodeDTOList)
                .build();
    }

    @Override
    @Transactional
    public MessageResponseDTO createSwiftCode(SwiftCodeCreateDTO createDto) {
        System.out.println(createDto);
        validator.validateBusinessRules(createDto);

        SwiftCode swiftCode = swiftCodeMapper.fromCreateDto(createDto);

        if (!createDto.isHeadquarter()) {
            Optional<SwiftCode> headquarter = findHeadquarterForBranch(createDto.getSwiftCode());
            headquarter.ifPresent(swiftCode::setHeadquarter);
        }

        swiftCodeRepository.save(swiftCode);

        return MessageResponseDTO.builder()
                .message("Swift code created successfully")
                .build();
    }

    @Override
    @Transactional
    public MessageResponseDTO deleteSwiftCode(String swiftCode) {
        validator.validateSwiftCode(swiftCode);

        SwiftCode swiftCodeEntity = swiftCodeRepository.findBySwiftCodeWithBranches(swiftCode)
                .orElseThrow(() -> new ResourceNotFoundException("Swift code not found: " + swiftCode));

        if (swiftCodeEntity.isHeadquarterFlag() && !swiftCodeEntity.getBranches().isEmpty()) {
            throw new BadRequestException("Cannot delete headquarters with active branches. Remove all branches first.");
        }

        swiftCodeRepository.deleteBySwiftCode(swiftCode);

        return MessageResponseDTO.builder()
                .message("Swift code deleted successfully")
                .build();
    }

    private Optional<SwiftCode> findHeadquarterForBranch(String branchSwiftCode) {
        if (branchSwiftCode.length() < 8) {
            return Optional.empty();
        }

        String baseCode = branchSwiftCode.substring(0, 8);
        return swiftCodeRepository.findHeadquarterByBaseCode(baseCode + "XXX");
    }
}