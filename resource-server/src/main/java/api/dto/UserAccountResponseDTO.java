package api.dto;

import api.enums.Status;

import java.time.LocalDate;
import java.util.UUID;

public record UserAccountResponseDTO

        (UUID clientCode,

         String name,

         String cpf,

         String email,

         String phone,

         int age,

         LocalDate birthDate,

         Status status) {}
