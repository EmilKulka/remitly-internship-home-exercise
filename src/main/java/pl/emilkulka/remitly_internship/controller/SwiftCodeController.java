package pl.emilkulka.remitly_internship.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.emilkulka.remitly_internship.dto.*;
import pl.emilkulka.remitly_internship.service.SwiftCodeService;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {

    private final SwiftCodeService swiftCodeService;

    @GetMapping("/{swift-code}")
    public ResponseEntity<SwiftCodeDTO> getSwiftCodeDetails(@PathVariable("swift-code") String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodeDetails(swiftCode));
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<CountrySwiftCodesDTO> getSwiftCodesByCountry(@PathVariable("countryISO2code") String countryISO2) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodesByCountry(countryISO2));
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createSwiftCode(@Valid @RequestBody SwiftCodeCreateDTO createDto) {
        return new ResponseEntity<>(swiftCodeService.createSwiftCode(createDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{swift-code}")
    public ResponseEntity<MessageResponseDTO> deleteSwiftCode(@PathVariable("swift-code") String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.deleteSwiftCode(swiftCode));
    }
}