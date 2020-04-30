package network.asimov.behavior.check.dorg;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mongodb.service.dorg.ProposalService;
import network.asimov.response.ResultView;
import network.asimov.util.TimeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-07
 */
@Component("daoVoteCheck")
public class VoteCheck implements CheckBehavior {
    @Resource(name = "daoProposalService")
    private ProposalService proposalService;

    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String contractAddress = (String) args.get("contractAddress");
        long proposalId = (long) args.get("proposalId");

        // Check that the organization exists and is in the correct state
        Optional<Organization> orgOptional = organizationService.getOrganizationByAddress(contractAddress);
        if (!orgOptional.isPresent()) {
            return ResultView.error(ErrorCode.ORGANIZATION_NOT_EXISTS_ERROR);
        }

        if (orgOptional.get().getStatus().equals(Organization.Status.Closed.ordinal())) {
            return ResultView.error(ErrorCode.ORGANIZATION_CLOSED_ERROR);
        }

        // Check whether the user is the organization member
        Optional<Member> optional = memberService.getInServiceMemberByAddress(address, contractAddress);
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        // Check that the proposed status is correct
        Optional<Proposal> proposalOptional = proposalService.getProposalById(proposalId, contractAddress);
        if (!proposalOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }

        if (proposalOptional.get().getEndTime() < TimeUtil.currentSeconds() || !proposalOptional.get().getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
            return ResultView.error(ErrorCode.PROPOSAL_EXPIRED);
        }
        return ResultView.ok();
    }
}
