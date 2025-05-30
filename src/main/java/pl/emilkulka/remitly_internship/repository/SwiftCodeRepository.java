package pl.emilkulka.remitly_internship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.emilkulka.remitly_internship.model.SwiftCode;

import java.util.List;
import java.util.Optional;

public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    Optional<SwiftCode> findBySwiftCode(String swiftCode);
    @Query("SELECT s FROM SwiftCode s LEFT JOIN FETCH s.branches WHERE s.swiftCode = :swiftCode")
    Optional<SwiftCode> findBySwiftCodeWithBranches(String swiftCode);
    @Query("SELECT s FROM SwiftCode s WHERE s.swiftCode = :swiftCode AND s.headquarterFlag = true")
    Optional<SwiftCode> findHeadquarterByBaseCode(@Param("swiftCode") String swiftCode);
    List<SwiftCode> findByCountryISO2(String countryISO2);
    boolean existsBySwiftCode(String swiftCode);
    void deleteBySwiftCode(String swiftCode);
}
