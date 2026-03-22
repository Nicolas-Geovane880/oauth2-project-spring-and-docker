package api.dto;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransferResponseDTO
        (Long id,

         String sourceName,

         String sourceCPF,

         String targetName,

         String targetCPF,

         BigDecimal value,

         LocalDateTime transferredAt) {
}
