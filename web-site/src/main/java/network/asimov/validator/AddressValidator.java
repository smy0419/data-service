package network.asimov.validator;

import network.asimov.util.AddressUtil;
import network.asimov.validator.annotation.AddressValidate;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author sunmengyuan
 * @date 2019-11-19
 */
public class AddressValidator implements ConstraintValidator<AddressValidate, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isNotBlank(s)) {
            return AddressUtil.validateAddress(s);
        } else {
            return true;
        }
    }
}
