package gateway.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record ApiRequestDTO
        (String name,

         String lastName,

         String cpf,

         String email,

         LocalDate birthDate,

         String phone) {}
