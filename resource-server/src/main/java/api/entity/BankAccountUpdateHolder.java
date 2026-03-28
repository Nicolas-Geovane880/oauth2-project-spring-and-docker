package api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table (name = "bank_account_update_holder")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountUpdateHolder {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "client_code", nullable = false, unique = true)
    @JdbcTypeCode (SqlTypes.VARCHAR)
    private UUID clientCode;

    @Column (name = "name")
    private String name;

    @Column (name = "last_name")
    private String lastName;

    @Column (name = "email")
    private String email;

    @Column (name = "phone")
    private String phone;

    @Column (name = "birth_date")
    private LocalDate birthDate;

    @Column (name = "age")
    private int age;

    @Column (name = "cpf")
    private String cpf;
}
