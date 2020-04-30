package network.asimov.validator.annotation;

import network.asimov.validator.AddressValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunmengyuan
 * @date 2019-11-19
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressValidator.class)
public @interface AddressValidate {
    String message() default "Illegal Address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
