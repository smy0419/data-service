package network.asimov.controller.dorg;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.service.dorg.DaoMessageService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.VoteCheckRequest;
import network.asimov.request.dorg.VoteRequest;
import network.asimov.response.ResultView;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2019-12-17
 */
@CrossOrigin
@RestController("daoVoteController")
@Api(tags = {"dao"})
@RequestMapping(path = "/dao/org", produces = RequestConstants.CONTENT_TYPE_JSON)
public class VoteController {
    @Resource(name = "daoVoteCheck")
    private CheckBehavior voteCheck;

    @Resource(name = "daoSendDeletableTransaction")
    private SendTransactionBehavior daoSendDeletableTransaction;

    @PostMapping("/vote/check")
    @ApiOperation(value = "Check vote")
    public ResultView checkVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                @Validated @RequestBody VoteCheckRequest voteCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateVote(address, voteCheckRequest.getContractAddress(), voteCheckRequest.getProposalId());
        return voteCheck.check(checkArgs);
    }

    @PostMapping("/vote")
    @ApiOperation(value = "Vote")
    public ResultView vote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                           @Validated @RequestBody VoteRequest voteRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateVote(address, voteRequest.getContractAddress(), voteRequest.getProposalId());
        ResultView check = voteCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.PROPOSAL_ID, voteRequest.getProposalId());
        additionalInfoMap.put(OperationAdditionalKey.DECISION, voteRequest.getDecision());

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.Vote.getCode(), additionalInfoMap, address, voteRequest.getContractAddress());
        return daoSendDeletableTransaction.perform(voteRequest.getCallData(), args);
    }
}
