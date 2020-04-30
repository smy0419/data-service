package network.asimov.behavior.check.foundation;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.service.foundation.MemberService;
import network.asimov.mongodb.service.foundation.ProposalService;
import network.asimov.mysql.constant.FoundationOperationType;
import network.asimov.mysql.service.foundation.FoundationOperationService;
import network.asimov.response.ResultView;
import network.asimov.util.TimeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-10-17
 */
@Component("foundationVoteCheck")
public class VoteCheck implements CheckBehavior {
    @Resource(name = "foundationMemberService")
    private MemberService memberService;

    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        long proposalId = (long) args.get("proposalId");

        Optional<Member> memberOptional = memberService.findInServiceMember(address);
        if (!memberOptional.isPresent()) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }
        Optional<Proposal> proposalOptional = proposalService.getProposalById(proposalId);
        if (!proposalOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }

        if (proposalOptional.get().getEndTime() < TimeUtil.currentSeconds()) {
            return ResultView.error(ErrorCode.PROPOSAL_EXPIRED);
        }

        if (!proposalOptional.get().getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
            return ResultView.error(ErrorCode.INVALID_PROPOSAL_STATUS);
        }

        boolean voteExist = foundationOperationService.applyExist(proposalId, address, FoundationOperationType.Vote.getCode());
        if (voteExist) {
            return ResultView.error(ErrorCode.REPEAT_OPERATION_ERROR);
        }
        return ResultView.ok();
    }
}
