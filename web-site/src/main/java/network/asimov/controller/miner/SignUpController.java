package network.asimov.controller.miner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.chainrpc.pojo.MinerBlockDTO;
import network.asimov.chainrpc.service.contract.ValidatorCommitteeService;
import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.service.miner.RoundService;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.request.RequestConstants;
import network.asimov.request.miner.SignUpRequest;
import network.asimov.response.ResultView;
import network.asimov.response.common.TxView;
import network.asimov.response.miner.ValidatorInfoView;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-09-29
 */

@CrossOrigin
@RestController("minerSignUpController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class SignUpController {
    @Resource(name = "validatorCommitteeService")
    private ValidatorCommitteeService validatorCommitteeService;

    @Resource(name = "minerSendDeletableTransaction")
    private SendTransactionBehavior minerSendDeletableTransaction;

    @Resource(name = "minerRoundService")
    private RoundService minerRoundService;

    @Resource(name = "minerSignUpCheck")
    private CheckBehavior minerSignUpCheck;

    @ApiOperation(value = "Check apply")
    @PostMapping(path = "/committee/apply/check")
    public ResultView checkSignUp(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        Map<String, Object> args = CheckArgsUtil.generate(address);
        return minerSignUpCheck.check(args);
    }

    @ApiOperation(value = "Apply", notes = "call /miner/contract returns abi => signUp()", consumes = RequestConstants.CONTENT_TYPE_JSON)
    @PostMapping(path = "/committee/apply", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<TxView> signUp(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                     @RequestBody @Validated SignUpRequest signUpRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generate(address);
        ResultView check = minerSignUpCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Round round = minerRoundService.getCurrentRound();
        Map<String, Object> additionalInfo = new HashMap<>(1);
        additionalInfo.put(OperationAdditionalKey.DECLARATION, signUpRequest.getDeclaration());
        Map<String, Object> sendTxArgs = SendTxArgsUtil.generate(round.getRound(), MinerOperationType.SignUp.getCode(), additionalInfo, address);

        return minerSendDeletableTransaction.perform(signUpRequest.getCallData(), sendTxArgs);
    }

    @ApiOperation(value = "Get applicant's produced block")
    @PostMapping(path = "/applicant/produced")
    public ResultView<ValidatorInfoView> getApplicantProduced(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        MinerBlockDTO minerStakeAndBlocks = validatorCommitteeService.getMinerBlocks(address);
        return ResultView.ok(ValidatorInfoView.builder().produced(minerStakeAndBlocks.getActualBlocks()).build());
    }
}
