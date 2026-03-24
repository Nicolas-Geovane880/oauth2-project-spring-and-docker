package api.entity;

import api.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table (name = "client")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode (SqlTypes.VARCHAR)
    private UUID code;

    @Column (name = "name", nullable = false)
    private String name;

    @Column (name = "last_name")
    private String lastName;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "phone", nullable = false, unique = true)
    private String phone;

    @Column (name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column (name = "age", nullable = false)
    private int age;

    @Column (name = "status", nullable = false)
    @Enumerated (value = EnumType.STRING)
    private Status status;

    @PrePersist
    protected void setUUID () {
        this.code = UUID.randomUUID();
        this.status = Status.PENDING;
    }
}
