package gateway.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record AccountRegisterDTO
        (String name,

         String lastName,

         String cpf,

         String password,

         String email,

         LocalDate birthDate,

         String phone) {}
