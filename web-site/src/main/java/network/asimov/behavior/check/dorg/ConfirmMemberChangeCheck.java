package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-04
 */
@Component("confirmMemberChangeCheck")
public class ConfirmMemberChangeCheck implements CheckBehavior {
    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        Integer changeType = (Integer) args.get("changeType");

        Optional<Organization> orgOptional = organizationService.getOrganizationByAddress(contractAddress);
        // check if the organization exists
        if (!orgOptional.isPresent()) {
            return ResultView.error(ErrorCode.ORGANIZATION_NOT_EXISTS_ERROR);
        }

        // check if organization status is normal
        if (orgOptional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        if (changeType.equals(DaoOperationType.ConfirmPresident.getCode())) {
            // check user is president
            if (orgOptional.get().getPresident().equals(address)) {
                return ResultView.error(ErrorCode.ACTION_FAILED);
            }
        } else if (changeType.equals(DaoOperationType.JoinNewMember.getCode())) {
            // check user is member
            Optional<Member> optionalMember = memberService.getInServiceMemberByAddress(address, contractAddress);
            if (optionalMember.isPresent()) {
                return ResultView.error(ErrorCode.MEMBER_STATUS_ERROR);
            }
        }
        return ResultView.ok();
    }
}
