package network.asimov.validator.annotation;

import network.asimov.validator.ProposalValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sunmengyuan
 * @date 2019-10-22
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProposalValidator.class)
public @interface ProposalValidate {
    String message() default "Illegal Parameter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
