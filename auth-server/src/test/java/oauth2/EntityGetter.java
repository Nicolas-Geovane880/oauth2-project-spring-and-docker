package oauth2;

import oauth2.dto.UserRegisterDTO;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.entity.UserStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EntityGetter {

    public UserRegisterDTO getUserRegisterDTO () {
        return UserRegisterDTO.builder()
                .name("name")
                .lastName("last name")
                .cpf("12345678900")
                .password("password")
                .email("user@mail.com")
                .phone("(xx) 1234-5678")
                .birthDate(LocalDate.now().minus(20, ChronoUnit.YEARS))
                .build();
    }

    public User getUser () {
        return User.builder()
                .cpf("12345678900")
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
