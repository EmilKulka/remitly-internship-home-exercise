package pl.emilkulka.remitly_internship.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeDetailsDTO extends SwiftCodeDTO{
    private List<BranchDTO> branches;
}
