package pl.emilkulka.remitly_internship.service.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SwiftCodeRelationshipProcessor implements SwiftCodeProcessor {

    @Override
    public List<SwiftCode> processSwiftCodes(List<SwiftCode> swiftCodes) {
        log.info("Processing relationships between {} SWIFT codes", swiftCodes.size());

        Map<String, SwiftCode> allCodesMap = new HashMap<>();
        for (SwiftCode code : swiftCodes) {
            allCodesMap.put(code.getSwiftCode(), code);
        }

        Map<String, SwiftCode> headquartersMap = buildHeadquartersMap(swiftCodes);
        associateBranchesWithHeadquarters(swiftCodes, headquartersMap);

        log.info("Relationship processing complete");
        return swiftCodes;
    }

    private Map<String, SwiftCode> buildHeadquartersMap(List<SwiftCode> swiftCodes) {
        Map<String, SwiftCode> headquartersMap = new HashMap<>();

        for (SwiftCode code : swiftCodes) {
            if (code.isHeadquarterFlag()) {
                headquartersMap.put(code.getSwiftCode().substring(0, 8), code);
            }
        }

        log.info("Found {} headquarters codes", headquartersMap.size());
        return headquartersMap;
    }

    private void associateBranchesWithHeadquarters(List<SwiftCode> allCodes, Map<String, SwiftCode> headquartersMap) {
        int associatedBranches = 0;

        for (SwiftCode code : allCodes) {
            if (!code.isHeadquarterFlag() && code.getSwiftCode().length() >= 8) {
                String prefix = code.getSwiftCode().substring(0, 8);
                SwiftCode headquarter = headquartersMap.get(prefix);

                if (headquarter != null) {
                    code.setHeadquarter(headquarter);
                    headquarter.getBranches().add(code);
                    associatedBranches++;
                }
            }
        }

        log.info("Associated {} branches with their headquarters", associatedBranches);
    }
}