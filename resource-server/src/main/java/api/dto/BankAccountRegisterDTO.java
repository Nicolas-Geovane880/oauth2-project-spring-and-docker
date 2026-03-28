package api.dto;

import api.annotations.ValidCPF;
import api.annotations.ValidEmail;
import api.annotations.ValidPhone;
import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record BankAccountRegisterDTO

        (@NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @Size(max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String name,

         @Size (max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String lastName,

         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @ValidCPF
         String cpf,

         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @ValidEmail
         String email,

         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @ValidPhone
         String phone,

         @NotNull (message = ErrorsMessage.FIELD_NOT_BLANK)
         LocalDate birthDate,

         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @Size (max = ConstantValue.PASSWORD_MAX_SIZE, min = ConstantValue.PASSWORD_MIN_SIZE, message = ErrorsMessage.PASSWORD_VALID_SIZE)
         String password) implements BankAccountProcessDTO {}
