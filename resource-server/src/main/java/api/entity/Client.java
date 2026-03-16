package api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column (name = "auth_user_id", nullable = false)
    private Long authUserId;
}
