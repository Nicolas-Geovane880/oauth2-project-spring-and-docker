package oauth2.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserRegisterDTO

        (String name,

         String lastName,

         String cpf,

         String password,

         String email,

         LocalDate birthDate,

         String phone) {}
