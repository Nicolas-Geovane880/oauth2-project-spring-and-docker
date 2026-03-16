package oauth2.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table (name = "user_status")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column (name = "is_active")
    private boolean isActive = true;

    @Builder.Default
    @Column (name = "is_account_non_locked")
    private boolean isAccountNonLocked = true;

    @Builder.Default
    @Column (name = "is_enabled")
    private boolean isEnabled = true;

    @CreationTimestamp
    @Column (name = "created_At")
    private Instant createdAt;

    @Column (name = "deleted_at")
    private Instant deletedAt;
}
