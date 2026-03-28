package api.dto;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ExtractResponseDTO

        (String name,

         String cpf,

         BigDecimal balance,

         BigDecimal remainingTransferLimitValue) {}
