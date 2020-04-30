package network.asimov.validator;

import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.request.foundation.ProposalDetail;
import network.asimov.validator.annotation.ProposalValidate;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author sunmengyuan
 * @date 2019-10-22
 */
public class ProposalValidator implements ConstraintValidator<ProposalValidate, ProposalDetail> {
    @Override
    public boolean isValid(ProposalDetail request, ConstraintValidatorContext constraintValidatorContext) {
        Integer proposalType = request.getProposalType();
        if (Proposal.Type.Expenses.ordinal() == proposalType) {
            if (request.getInvestAmount() == null || StringUtils.isEmpty(request.getInvestAsset())) {
                return false;
            }
        }
        return true;
    }
}
