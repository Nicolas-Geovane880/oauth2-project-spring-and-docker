package api.dto;

import api.annotations.ValidCPF;
import api.annotations.ValidEmail;
import api.annotations.ValidPhone;
import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@JsonInclude (JsonInclude.Include.NON_EMPTY)
public record UserAccountUpdateDTO

        (@Size(max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String name,

         @Size (max = ConstantValue.NAME_MAX_SIZE, min = ConstantValue.NAME_MIN_SIZE, message = ErrorsMessage.NAME_VALID_SIZE)
         String lastName,

         @ValidCPF
         String cpf,

         @ValidEmail
         String email,

         @ValidPhone
         String phone,

         LocalDate birthDate,

         @Size (max = ConstantValue.PASSWORD_MAX_SIZE, min = ConstantValue.PASSWORD_MIN_SIZE, message = ErrorsMessage.PASSWORD_VALID_SIZE)
         String password) implements BankAccountProcessDTO {}
