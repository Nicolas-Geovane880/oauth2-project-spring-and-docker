package gateway.annotations.constraint;

import gateway.annotations.ValidCPF;
import gateway.constant.ErrorsMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Valid;

public class ValidCPFConstraint implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (cpf == null || cpf.isEmpty()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.FIELD_NOT_BLANK)
                    .addConstraintViolation();
            return false;
        }

        if (cpf.length() != 11) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.CPF_VALID_SIZE)
                    .addConstraintViolation();
            return false;
        }

        String regex = "\\d{11}";

        if (!cpf.matches(regex)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.CPF_MUST_BE_NUMERAL)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
