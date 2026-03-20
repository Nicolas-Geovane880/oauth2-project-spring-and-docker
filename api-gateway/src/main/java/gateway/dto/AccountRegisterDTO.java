package gateway.dto;

import gateway.annotations.ValidCPF;
import gateway.annotations.ValidEmail;
import gateway.annotations.ValidPhone;
import gateway.constant.ConstantValue;
import gateway.constant.ErrorsMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.apache.tomcat.util.bcel.Const;

import java.time.LocalDate;

@Builder
public record AccountRegisterDTO
        (
         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @Size (max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String name,

         @Size (max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String lastName,

         @ValidCPF
         String cpf,

         @NotBlank (message = ErrorsMessage.FIELD_NOT_BLANK)
         @Size (max = ConstantValue.PASSWORD_MAX_SIZE, min = ConstantValue.PASSWORD_MIN_SIZE, message = ErrorsMessage.PASSWORD_VALID_SIZE)
         String password,

         @ValidEmail
         String email,

         LocalDate birthDate,

         @ValidPhone
         String phone) {}
