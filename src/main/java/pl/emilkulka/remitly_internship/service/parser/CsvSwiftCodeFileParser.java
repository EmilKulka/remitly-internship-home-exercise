package pl.emilkulka.remitly_internship.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CsvSwiftCodeFileParser implements SwiftCodeFileParser {

    private final String swiftDataFile;
    private final SwiftCodeLineParser lineParser;

    public CsvSwiftCodeFileParser(
            @Value("${swift.data.file:data/swift-code-data.csv}") String swiftDataFile,
            SwiftCodeLineParser lineParser) {
        this.swiftDataFile = swiftDataFile;
        this.lineParser = lineParser;
    }

    @Override
    public List<SwiftCode> parseFile() throws IOException {
        log.info("Parsing SWIFT codes from CSV file: {}", swiftDataFile);
        List<SwiftCode> parsedCodes = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource(swiftDataFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = reader.readLine();
            if (line == null) {
                log.warn("Empty file: {}", swiftDataFile);
                return parsedCodes;
            }

            while ((line = reader.readLine()) != null) {
                SwiftCode swiftCode = lineParser.parseLine(line);
                if (swiftCode != null) {
                    parsedCodes.add(swiftCode);
                }
            }
        }

        log.info("Successfully parsed {} SWIFT codes from file", parsedCodes.size());
        return parsedCodes;
    }
}