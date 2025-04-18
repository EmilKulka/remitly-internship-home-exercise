package pl.emilkulka.remitly_internship.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.emilkulka.remitly_internship.model.SwiftCode;
import pl.emilkulka.remitly_internship.repository.SwiftCodeRepository;
import pl.emilkulka.remitly_internship.service.parser.SwiftCodeFileParser;
import pl.emilkulka.remitly_internship.service.processor.SwiftCodeProcessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeParserServiceTest {

    @Mock
    private SwiftCodeFileParser fileParser;

    @Mock
    private SwiftCodeProcessor processor;

    @Mock
    private SwiftCodeRepository repository;

    @InjectMocks
    private SwiftCodeParserService service;

    @Captor
    private ArgumentCaptor<Set<SwiftCode>> swiftCodeCaptor;

    @Test
    void shouldOrchestrateParsingSavingProcess() throws Exception {
        // Given
        SwiftCode headquarter1 = SwiftCode.builder()
                .swiftCode("ABC")
                .headquarterFlag(true)
                .build();

        SwiftCode branch1 = SwiftCode.builder()
                .swiftCode("DEF")
                .headquarterFlag(false)
                .headquarter(headquarter1)
                .build();

        headquarter1.getBranches().add(branch1);

        List<SwiftCode> parsedCodes = Arrays.asList(headquarter1, branch1);
        List<SwiftCode> processedCodes = Arrays.asList(headquarter1, branch1);

        when(fileParser.parseFile()).thenReturn(parsedCodes);
        when(processor.processSwiftCodes(parsedCodes)).thenReturn(processedCodes);

        // When
        service.parseAndSaveSwiftCodes();

        // Then
        verify(fileParser).parseFile();
        verify(processor).processSwiftCodes(parsedCodes);

        verify(repository, times(2)).saveAll(swiftCodeCaptor.capture());

        List<Set<SwiftCode>> capturedSets = swiftCodeCaptor.getAllValues();
        assertEquals(2, capturedSets.size());

        assertTrue(capturedSets.get(0).stream().allMatch(SwiftCode::isHeadquarterFlag));
        assertTrue(capturedSets.get(0).contains(headquarter1));

        assertTrue(capturedSets.get(1).stream().noneMatch(SwiftCode::isHeadquarterFlag));
        assertTrue(capturedSets.get(1).contains(branch1));
    }

    @Test
    void shouldHandleExceptionsGracefully() throws Exception {
        // Given
        when(fileParser.parseFile()).thenThrow(new RuntimeException("Test exception"));

        // When
        try {
            service.parseAndSaveSwiftCodes();
        } catch (Exception e) {
            // Expected exception - we rethrow in the service now
        }

        // Then
        verify(repository, never()).saveAll(any());
    }

    private void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + " but was: " + actual);
        }
    }
}