package pl.emilkulka.remitly_internship.service.processor;

import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.List;

public interface SwiftCodeProcessor {
    List<SwiftCode> processSwiftCodes(List<SwiftCode> swiftCodes);
}