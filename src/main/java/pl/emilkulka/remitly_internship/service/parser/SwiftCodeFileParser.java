package pl.emilkulka.remitly_internship.service.parser;

import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.List;

public interface SwiftCodeFileParser {
    List<SwiftCode> parseFile() throws Exception;
}
