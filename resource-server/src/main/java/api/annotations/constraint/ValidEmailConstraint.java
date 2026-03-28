package api.annotations.constraint;

import api.annotations.ValidEmail;
import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidEmailConstraint implements ConstraintValidator<ValidEmail, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (email != null && (email.length() < ConstantValue.EMAIL_MIN_SIZE || email.length() > ConstantValue.EMAIL_MAX_SIZE)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.EMAIL_VALID_SIZE)
                    .addConstraintViolation();
            return false;
        }

        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

        if (email != null && !email.matches(regex)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.EMAIL_VALID)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
