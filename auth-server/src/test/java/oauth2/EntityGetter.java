package oauth2;

import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;
import java.util.List;
import java.util.UUID;

public class EntityGetter {

    public User getUser () {
        return User.builder()
                .cpf("12345678900")
                .code(UUID.randomUUID())
                .password("EncodedPassword")
                .roles(List.of(getUserRole(), getAdminRole()))
                .userStatus(new UserStatus())
                .build();
    }

    public Role getUserRole () {
        return Role.builder()
                .id(2L)
                .role("ROLE_USER")
                .build();
    }

    public Role getAdminRole () {
        return Role.builder()
                .id(1L)
                .role("ROLE_ADMIN")
                .build();
    }
}
