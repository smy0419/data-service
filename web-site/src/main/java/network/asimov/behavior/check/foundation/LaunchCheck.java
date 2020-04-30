package network.asimov.behavior.check.foundation;

import com.alibaba.fastjson.JSON;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.service.foundation.MemberService;
import network.asimov.mongodb.service.foundation.ProposalService;
import network.asimov.mysql.constant.FoundationOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
import network.asimov.mysql.service.foundation.FoundationOperationService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-10-17
 */

@Component("foundationLaunchProposalCheck")
public class LaunchCheck implements CheckBehavior {
    @Resource(name = "foundationMemberService")
    private MemberService memberService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");
        String targetAddress = (String) args.get("targetAddress");
        int proposalType = (int) args.get("proposalType");

        // Check whether the sponsor is a current serving member of the council
        Optional<Member> member = memberService.findInServiceMember(address);
        if (!member.isPresent()) {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        // Query target address
        Optional<Member> target = memberService.findInServiceMember(targetAddress);

        if (proposalType == Proposal.Type.Expenses.ordinal()) {
            long value = this.getFoundationAsimAmount();
            Long investAmount = (Long) args.get("amount");
            if (investAmount == null) {
                return ResultView.error(ErrorCode.PARAMETER_INVALID.getCode(), String.format(ErrorCode.PARAMETER_INVALID.getMsg(), "invest_amount"));
            }
            // Check that the foundation balance is sufficient
            if (value < investAmount) {
                return ResultView.error(ErrorCode.NOT_ENOUGH_AMOUNT);
            }
        }

        if (proposalType == Proposal.Type.Elect.ordinal()) {
            if (target.isPresent()) {
                return ResultView.error(ErrorCode.MEMBER_STATUS_ERROR);
            }
        }

        if (proposalType == Proposal.Type.Impeach.ordinal()) {
            if (!target.isPresent()) {
                return ResultView.error(ErrorCode.MEMBER_STATUS_ERROR);
            }
            List<String> memberList = memberService.listAddress();
            if (!memberList.isEmpty() && memberList.size() == 3) {
                return ResultView.error(ErrorCode.NOT_ENOUGH_MEMBER);
            }
        }

        boolean exist = true;
        if (proposalType == Proposal.Type.Elect.ordinal() || proposalType == Proposal.Type.Impeach.ordinal()) {
            List<TFoundationOperation> list = foundationOperationService.listByOperateType(FoundationOperationType.Proposal.getCode());
            if (list.isEmpty()) {
                exist = false;
            }

            for (TFoundationOperation operation : list) {
                int proposalTypeInDb = JSON.parseObject(operation.getAdditionalInfo()).getIntValue(OperationAdditionalKey.PROPOSAL_TYPE);
                String addressInDb = JSON.parseObject(operation.getAdditionalInfo()).getString(OperationAdditionalKey.TARGET_ADDRESS);
                if (addressInDb != null && targetAddress.equals(addressInDb) && proposalTypeInDb == proposalType) {
                    if ((int) operation.getTxStatus() == TxStatus.Pending.ordinal()) {
                        exist = true;
                        break;
                    } else if ((int) operation.getTxStatus() == TxStatus.Success.ordinal()) {
                        String txHash = operation.getTxHash();
                        Optional<Proposal> optionalProposal = proposalService.getProposalByHash(txHash);
                        if (optionalProposal.isPresent()) {
                            Proposal proposal = optionalProposal.get();
                            if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                                exist = true;
                                break;
                            } else if (proposal.getStatus().equals(Proposal.Status.Approved.ordinal())) {
                                if (proposalType == Proposal.Type.Impeach.ordinal() && !target.isPresent() || proposalType == Proposal.Type.Elect.ordinal() && target.isPresent()) {
                                    exist = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                exist = false;
            }
        } else {
            exist = false;
        }

        if (exist) {
            return ResultView.error(ErrorCode.REPEAT_OPERATION_ERROR);
        }

        return ResultView.ok();
    }

    private long getFoundationAsimAmount() {
        long value = 0;
        List<AssetDTO> assetDTOS = balanceService.listBalance(ChainConstant.GENESIS_ORGANIZATION_ADDRESS);
        for (AssetDTO assetDTO : assetDTOS) {
            if (assetDTO.getAsset().equals(ChainConstant.ASSET_ASIM)) {
                value = Long.parseLong(assetDTO.getValue());
                break;
            }
        }
        return value;
    }
}
