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
@Component("memberChangeCheck")
public class MemberChangeCheck implements CheckBehavior {
    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        String targetAddress = (String) args.get("targetAddress");
        Integer changeType = (Integer) args.get("changeType");

        Optional<Organization> optional = organizationService.getOrganizationByAddress(contractAddress);
        // Check that the organization exists and is in the correct state
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.ORGANIZATION_NOT_EXISTS_ERROR);
        }

        if (optional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        // Check whether the user is the organization chairman
        if (!optional.get().getPresident().equals(address)) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        Optional<Member> optionalMember = memberService.getInServiceMemberByAddress(targetAddress, contractAddress);
        if (changeType.equals(DaoOperationType.InviteNewMember.getCode())) {
            if (optionalMember.isPresent()) {
                return ResultView.error(ErrorCode.MEMBER_STATUS_ERROR);
            }
        } else if (changeType.equals(DaoOperationType.InviteNewPresident.getCode())) {
            if (optional.get().getPresident().equals(targetAddress)) {
                return ResultView.error(ErrorCode.ACTION_FAILED);
            }
        } else if (changeType.equals(DaoOperationType.RemoveMember.getCode())) {
            if (!optionalMember.isPresent()) {
                return ResultView.error(ErrorCode.MEMBER_STATUS_ERROR);
            }
        }
        return ResultView.ok();
    }
}
