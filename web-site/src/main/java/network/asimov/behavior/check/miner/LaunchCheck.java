package network.asimov.behavior.check.miner;

import com.alibaba.fastjson.JSON;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.constant.Condition;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.miner.Member;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.service.miner.MemberService;
import network.asimov.mongodb.service.miner.ProposalService;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.response.ResultView;
import network.asimov.util.TimeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-10-28
 */
@Component("minerLaunchCheck")
public class LaunchCheck implements CheckBehavior {
    @Resource(name = "minerMemberService")
    private MemberService memberService;

    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String asset = (String) args.get("asset");

        Optional<Member> optionalMember = memberService.getCurrentMiningMember(address);
        if (!optionalMember.isPresent()) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }
        if (optionalMember.get().getProduced() == 0) {
            return ResultView.error(ErrorCode.NOT_ENOUGH_PRODUCED);
        }

        if (asset.equals(ChainConstant.ASSET_ASIM)) {
            return ResultView.error(ErrorCode.ACTION_FAILED);
        }
        boolean exist = true;
        List<TMinerOperation> list = minerOperationService.listAssetProposal(MinerOperationType.Proposal.getCode());
        if (list.isEmpty()) {
            exist = false;
        }
        for (TMinerOperation operation : list) {
            String assetInDb = JSON.parseObject(operation.getAdditionalInfo()).getString(OperationAdditionalKey.ASSET);
            if (assetInDb != null && asset.equals(assetInDb)) {
                if ((int) operation.getTxStatus() == TxStatus.Pending.ordinal()) {
                    exist = true;
                    break;
                } else if ((int) operation.getTxStatus() == TxStatus.Success.ordinal()) {
                    String txHash = operation.getTxHash();
                    Optional<Proposal> optionalProposal = proposalService.getProposalByHash(txHash);
                    if (optionalProposal.isPresent()) {
                        Proposal proposal = optionalProposal.get();
                        if (proposal.getStatus().equals(Proposal.Status.Effective.ordinal()) || proposal.getStatus().equals(Proposal.Status.Approved.ordinal())) {
                            exist = true;
                            break;
                        } else if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal()) && proposal.getEndTime() > TimeUtil.currentSeconds()) {
                            exist = true;
                            break;
                        }
                    }
                }
            }
            exist = false;
        }
        if (exist) {
            return ResultView.error(ErrorCode.REPEAT_OPERATION_ERROR);
        }

        // 检查目前已生效的手续费币种
        List<String> txHashList = list.stream().map(TMinerOperation::getTxHash).collect(Collectors.toList());
        List<Proposal> proposalList = proposalService.listProposalByHash(txHashList);

        if (proposalList.size() >= Condition.ASSET_MAXIMUM) {
            return ResultView.error(ErrorCode.ASSET_EXCEED_MAXIMUM);
        }

        return ResultView.ok();
    }
}
