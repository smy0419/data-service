package network.asimov.behavior.check.miner;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.service.miner.MemberService;
import network.asimov.mongodb.service.miner.ProposalService;
import network.asimov.mongodb.service.miner.RoundService;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
@Component("minerVoteCheck")
public class VoteCheck implements CheckBehavior {
    @Resource(name = "minerMemberService")
    private MemberService memberService;

    @Resource(name = "minerRoundService")
    private RoundService roundService;

    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        long proposalId = (long) args.get("proposalId");

        boolean memberExist = memberService.exist(address);
        if (!memberExist) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }
        Round round = roundService.getCurrentRound();
        Optional<Proposal> optional = proposalService.getProposalById(proposalId);
        long nowSecond = System.currentTimeMillis() / 1000;
        if (!optional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        } else if (!optional.get().getRound().equals(round.getRound()) || optional.get().getEndTime() < nowSecond) {
            return ResultView.error(ErrorCode.PROPOSAL_EXPIRED);
        } else if (!optional.get().getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
            return ResultView.error(ErrorCode.INVALID_PROPOSAL_STATUS);
        }

        boolean voteExist = minerOperationService.applyExist(optional.get().getRound(), proposalId, address, MinerOperationType.Vote.getCode());
        if (voteExist) {
            return ResultView.error(ErrorCode.REPEAT_OPERATION_ERROR);
        }
        return ResultView.ok();
    }
}
