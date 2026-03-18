package api.dto;

import java.time.LocalDate;

public record ApiRequestDTO
        (String name,

         String lastName,

         String cpf,

         String email,

         LocalDate birthDate,

         String phone,

         Long authUserId) {}
