package api.dto;

import java.math.BigDecimal;

public record TransferCreateDTO

        (String targetCPF,

         BigDecimal value) {
}
