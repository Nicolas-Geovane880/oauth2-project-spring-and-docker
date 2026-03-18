package gateway.dto;

import lombok.Builder;

@Builder
public record ProfileValidationDTO

        (String email,

         String phone) {}
