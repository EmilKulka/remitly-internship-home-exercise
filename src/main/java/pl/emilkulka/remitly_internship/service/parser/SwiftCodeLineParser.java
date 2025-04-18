package pl.emilkulka.remitly_internship.service.parser;

import pl.emilkulka.remitly_internship.model.SwiftCode;

public interface SwiftCodeLineParser {
    SwiftCode parseLine(String line);
}