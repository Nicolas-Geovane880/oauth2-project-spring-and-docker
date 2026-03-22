package api.dto;

import java.math.BigDecimal;

public record TransferRequestDTO

        (String targetCPF,

         BigDecimal value) {
}
