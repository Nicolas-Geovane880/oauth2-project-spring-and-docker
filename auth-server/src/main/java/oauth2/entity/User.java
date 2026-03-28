package oauth2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table (name = "users")
@Getter
@Setter
@EqualsAndHashCode (of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "code", nullable = false, unique = true, updatable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID code;

    @Column (name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column (name = "password", nullable = false)
    private String password;

    @JsonIgnore
    @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn (name = "user_status_id", referencedColumnName = "id")
    private UserStatus userStatus;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name = "user_role",
                joinColumns = {@JoinColumn (name = "user_id")},
                inverseJoinColumns = {@JoinColumn (name = "role_id")})
    private List<Role> roles;

    public User (UUID clientCode, String cpf, String encodedPassword, UserStatus userStatus) {
        this.cpf = cpf;
        this.password = encodedPassword;
        this.userStatus = userStatus;
        this.roles = new ArrayList<>();
        this.code = clientCode;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.cpf;
    }
}
