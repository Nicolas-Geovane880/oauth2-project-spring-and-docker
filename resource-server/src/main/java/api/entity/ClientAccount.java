package api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table (name = "client_account")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientAccount {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "cpf", nullable = false, unique = true)
    private String cpf;

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "client_id", referencedColumnName = "id")
    private Client client;

    @Column (name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToOne (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn (name = "account_transfer_lock_id", referencedColumnName = "id")
    private AccountTransferLock accountTransferLock;

    public ClientAccount(Client client, String cpf) {
        this.accountTransferLock = new AccountTransferLock();
        this.balance = new BigDecimal("5000");
        this.client = client;
        this.cpf = cpf;
    }

    public void discountBalance (BigDecimal value) {
        if (this.balance.compareTo(value) < 0) throw new IllegalArgumentException("Insufficient balance.");
        this.balance = this.balance.subtract(value);
    }

    public void addBalance (BigDecimal value) {
        this.balance = this.balance.add(value);
    }
}
