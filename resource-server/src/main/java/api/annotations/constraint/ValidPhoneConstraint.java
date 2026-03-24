package api.annotations.constraint;

import api.annotations.ValidPhone;
import api.constant.ErrorsMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPhoneConstraint implements ConstraintValidator<ValidPhone, String> {


    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if (phone == null || phone.isEmpty()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.FIELD_NOT_BLANK)
                    .addConstraintViolation();
            return false;
        }

        String regex = "^(\\+55\\s?)?(\\(?\\d{2}\\)?\\s?)(9\\d{4}|\\d{4})-?\\d{4}$";

        if (!phone.matches(regex)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorsMessage.PHONE_VALID)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
