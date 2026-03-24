package gateway.dto;

import java.util.UUID;

public record AuthRequestDTO

        (String cpf,

         String password,

         UUID clientCode) {}
