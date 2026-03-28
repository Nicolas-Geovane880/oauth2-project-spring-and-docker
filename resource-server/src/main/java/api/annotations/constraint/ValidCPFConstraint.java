package api.annotations.constraint;

import api.annotations.ValidCPF;
import api.constant.ErrorsMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCPFConstraint implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (cpf != null && cpf.length() != 11) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.CPF_VALID_SIZE)
                    .addConstraintViolation();
            return false;
        }

        String regex = "\\d{11}";

        if (cpf != null && !cpf.matches(regex)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.CPF_MUST_BE_NUMERAL)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
