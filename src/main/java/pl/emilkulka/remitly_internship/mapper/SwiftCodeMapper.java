package pl.emilkulka.remitly_internship.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.emilkulka.remitly_internship.dto.SwiftCodeBranchDTO;
import pl.emilkulka.remitly_internship.dto.SwiftCodeCreateDTO;
import pl.emilkulka.remitly_internship.dto.SwiftCodeDTO;
import pl.emilkulka.remitly_internship.model.SwiftCode;
import pl.emilkulka.remitly_internship.dto.SwiftCodeWithBranchesDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SwiftCodeMapper {

    SwiftCodeDTO toDto(SwiftCode swiftCode);

    @Mapping(target = "branches", source = "branches", qualifiedByName = "toBranchDtoList")
    SwiftCodeWithBranchesDTO toWithBranchesDto(SwiftCode swiftCode);

    @Named("toBranchDtoList")
    List<SwiftCodeBranchDTO> toBranchDtoList(List<SwiftCode> branches);

    @Mapping(target = "headquarter", ignore = true)
    @Mapping(target = "branches", ignore = true)
    SwiftCode fromCreateDto(SwiftCodeCreateDTO createDto);

    @Mapping(target = "isHeadquarter", source = "headquarter", qualifiedByName = "isHeadquarter")
    SwiftCodeBranchDTO toBranchDto(SwiftCode swiftCode);

    @Named("isHeadquarter")
    default boolean isHeadquarter(SwiftCode headquarter) {
        return headquarter == null;
    }

    List<SwiftCodeDTO> toDtoList(List<SwiftCode> swiftCodes);
}