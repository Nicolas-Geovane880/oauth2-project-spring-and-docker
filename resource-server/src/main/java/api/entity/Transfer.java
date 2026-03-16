package api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table (name = "transfer")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "source_client_account_id", referencedColumnName = "id")
    private ClientAccount sourceAccount;

    @ManyToOne
    @JoinColumn (name = "target_client_account_id", referencedColumnName = "id")
    private ClientAccount targetAccount;

    @Column (name = "value", nullable = false)
    private Double value;
}
