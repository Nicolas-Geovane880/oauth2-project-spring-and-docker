package api.annotations;

import api.annotations.constraint.ValidCPFConstraint;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (value = RetentionPolicy.RUNTIME)
@Target (value = ElementType.FIELD)
@Constraint (validatedBy = ValidCPFConstraint.class)
public @interface ValidCPF {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
