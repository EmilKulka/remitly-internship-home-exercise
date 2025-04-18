package pl.emilkulka.remitly_internship.service.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CsvSwiftCodeLineParserTest {

    @Autowired
    private CsvSwiftCodeLineParser parser;

    @Test
    void shouldParseValidLine() {
        // Given
        String line = "AL,AAISALTRXXX,BIC11,UNITED BANK OF ALBANIA SH.A,\"HYRJA 3 RR. DRITAN HOXHA ND. 11 TIRANA, TIRANA, 1023\",TIRANA,ALBANIA,Europe/Tirane";

        // When
        SwiftCode result = parser.parseLine(line);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSwiftCode()).isEqualTo("AAISALTRXXX");
        assertThat(result.getBankName()).isEqualTo("UNITED BANK OF ALBANIA SH.A");
        assertThat(result.getCountryISO2()).isEqualTo("AL");
        assertThat(result.getCountryName()).isEqualTo("ALBANIA");
        assertThat(result.isHeadquarterFlag()).isTrue();
    }

    @Test
    void shouldIdentifyHeadquarterCorrectly() {
        // Given
        String headquarterLine = "AL,AAISALTRXXX,BIC11,BANK,ADDRESS,TOWN,COUNTRY,TIMEZONE";
        String branchLine = "AL,AAISALTR123,BIC11,BANK,ADDRESS,TOWN,COUNTRY,TIMEZONE";

        // When
        SwiftCode headquarter = parser.parseLine(headquarterLine);
        SwiftCode branch = parser.parseLine(branchLine);

        // Then
        assertThat(headquarter.isHeadquarterFlag()).isTrue();
        assertThat(branch.isHeadquarterFlag()).isFalse();
    }

    @Test
    void shouldHandleInvalidLine() {
        // Given
        String invalidLine = "AL,AAISALTRXXX";

        // When
        SwiftCode result = parser.parseLine(invalidLine);

        // Then
        assertThat(result).isNull();
    }
}
