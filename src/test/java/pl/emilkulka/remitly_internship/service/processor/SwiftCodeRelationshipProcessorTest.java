package pl.emilkulka.remitly_internship.service.processor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SwiftCodeRelationshipProcessorTest {

    @Autowired
    private SwiftCodeRelationshipProcessor processor;

    @Test
    void shouldAssociateBranchesWithHeadquarters() {
        // Given
        SwiftCode headquarter = SwiftCode.builder()
                .swiftCode("ABCDEFGHXXX")
                .headquarterFlag(true)
                .build();

        SwiftCode branch1 = SwiftCode.builder()
                .swiftCode("ABCDEFGH123")
                .headquarterFlag(false)
                .build();

        SwiftCode branch2 = SwiftCode.builder()
                .swiftCode("ABCDEFGH456")
                .headquarterFlag(false)
                .build();

        SwiftCode otherBank = SwiftCode.builder()
                .swiftCode("ZYXWVUTXXX")
                .headquarterFlag(true)
                .build();

        List<SwiftCode> swiftCodes = Arrays.asList(headquarter, branch1, branch2, otherBank);

        // When
        List<SwiftCode> result = processor.processSwiftCodes(swiftCodes);

        // Then
        assertThat(result).hasSize(4);

        assertThat(headquarter.getBranches()).hasSize(2);
        assertThat(headquarter.getBranches()).contains(branch1, branch2);
        assertThat(otherBank.getBranches()).isEmpty();

        assertThat(branch1.getHeadquarter()).isEqualTo(headquarter);
        assertThat(branch2.getHeadquarter()).isEqualTo(headquarter);
    }
}