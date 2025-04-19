package pl.emilkulka.remitly_internship.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.emilkulka.remitly_internship.dto.*;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SwiftCodeMapper {

    @Mapping(source = "headquarterFlag", target = "isHeadquarter")
    SwiftCodeDTO toSwiftCodeDTO(SwiftCode swiftCode);

    @Mapping(source = "headquarterFlag", target = "isHeadquarter")
    BranchDTO toBranchDTO(SwiftCode swiftCode);
    @Mapping(source = "headquarterFlag", target = "isHeadquarter")
    @Mapping(target = "branches", source = "branches", qualifiedByName = "toBranchDtoList")
    SwiftCodeDetailsDTO toSwiftCodeDetailsDTO(SwiftCode swiftCode);


    @Named("toBranchDtoList")
    default List<BranchDTO> toBranchDtoList(List<SwiftCode> branches) {
        if (branches == null) {
            return List.of();
        }
        return branches.stream()
                .map(this::toBranchDTO)
                .toList();
    }

    @Mapping(source = "headquarter", target = "headquarterFlag")
    @Mapping(target = "headquarter", ignore = true)
    @Mapping(target = "branches", ignore = true)
    SwiftCode fromCreateDto(SwiftCodeCreateDTO createDto);

    default List<CountrySwiftCodeDTO> toCountrySwiftCodeDTOList(List<SwiftCode> swiftCodes) {
        if (swiftCodes == null) {
            return List.of();
        }
        return swiftCodes.stream()
                .map(this::toCountrySwiftCodeDTO)
                .toList();
    }

    @Mapping(source = "headquarterFlag", target = "isHeadquarter")
    CountrySwiftCodeDTO toCountrySwiftCodeDTO(SwiftCode swiftCode);
}