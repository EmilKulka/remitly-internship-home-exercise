package pl.emilkulka.remitly_internship.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

@Component
@Slf4j
public class CsvSwiftCodeLineParser implements SwiftCodeLineParser {

    @Override
    public SwiftCode parseLine(String line) {
        try (CSVParser parser = CSVParser.parse(new StringReader(line), CSVFormat.DEFAULT)) {
            CSVRecord record = parser.getRecords().getFirst();
            if (record.size() < 8) {
                log.warn("Skipping invalid line with insufficient fields: {}", line);
                return null;
            }

            String countryISO2 = record.get(0).trim().toUpperCase();
            String swiftCode = record.get(1).trim();
            String bankName = record.get(3).trim();
            String address = record.get(4).trim();
            String countryName = record.get(6).trim().toUpperCase();
            boolean isHQ = swiftCode.endsWith("XXX");

            return SwiftCode.builder()
                    .swiftCode(swiftCode)
                    .bankName(bankName)
                    .address(address)
                    .countryISO2(countryISO2)
                    .countryName(countryName)
                    .headquarterFlag(isHQ)
                    .branches(new ArrayList<>())
                    .build();

        } catch (IOException | IllegalArgumentException e) {
            log.warn("Skipping invalid line: {}", line, e);
            return null;
        }
    }
}
