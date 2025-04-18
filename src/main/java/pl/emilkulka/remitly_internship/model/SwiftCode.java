package pl.emilkulka.remitly_internship.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "swift_codes")
@Getter
@Setter
@ToString(exclude = {"branches", "headquarter"})
@EqualsAndHashCode(exclude = {"branches", "headquarter"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCode {

    @Id
    @Column(name = "swift_code", nullable = false, unique = true)
    private String swiftCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "address")
    private String address;

    @Column(name = "country_iso2", nullable = false)
    private String countryISO2;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "is_headquarter", nullable = false)
    private boolean headquarterFlag;

    @Builder.Default
    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SwiftCode> branches = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarter_code")
    private SwiftCode headquarter;
}
