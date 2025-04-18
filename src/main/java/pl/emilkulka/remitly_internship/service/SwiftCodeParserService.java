package pl.emilkulka.remitly_internship.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.emilkulka.remitly_internship.model.SwiftCode;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;
import pl.emilkulka.remitly_internship.service.parser.SwiftCodeFileParser;
import pl.emilkulka.remitly_internship.service.processor.SwiftCodeProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Profile(("!test"))
public class SwiftCodeParserService {

    private final SwiftCodeRepository swiftCodeRepository;
    private final SwiftCodeFileParser fileParser;
    private final SwiftCodeProcessor processor;

    public SwiftCodeParserService(
            SwiftCodeRepository swiftCodeRepository,
            SwiftCodeFileParser fileParser,
            SwiftCodeProcessor processor) {
        this.swiftCodeRepository = swiftCodeRepository;
        this.fileParser = fileParser;
        this.processor = processor;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void parseAndSaveSwiftCodes() throws Exception {
        log.info("Starting the SWIFT codes parsing process");

        try {
            List<SwiftCode> parsedCodes = fileParser.parseFile();
            log.info("Parsed {} SWIFT codes from file", parsedCodes.size());

            List<SwiftCode> processedCodes = processor.processSwiftCodes(parsedCodes);

            Set<SwiftCode> headquarters = new HashSet<>();
            for (SwiftCode code : processedCodes) {
                if (code.isHeadquarterFlag()) {
                    headquarters.add(code);
                }
            }

            log.info("Saving {} headquarters SWIFT codes", headquarters.size());
            swiftCodeRepository.saveAll(headquarters);

            Set<SwiftCode> branches = new HashSet<>();
            for (SwiftCode code : processedCodes) {
                if (!code.isHeadquarterFlag()) {
                    branches.add(code);
                }
            }

            log.info("Saving {} branch SWIFT codes", branches.size());
            swiftCodeRepository.saveAll(branches);

            log.info("Successfully saved {} SWIFT codes to database", processedCodes.size());
        } catch (Exception e) {
            log.error("Error in SWIFT codes processing pipeline", e);
            throw e;
        }
    }
}