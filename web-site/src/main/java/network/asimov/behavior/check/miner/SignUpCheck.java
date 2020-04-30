package network.asimov.behavior.check.miner;

import network.asimov.behavior.check.CheckBehavior;
import network.asimov.chainrpc.pojo.MinerBlockDTO;
import network.asimov.chainrpc.service.contract.ValidatorCommitteeService;
import network.asimov.constant.Condition;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.service.miner.RoundService;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.response.ResultView;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
@Component("minerSignUpCheck")
public class SignUpCheck implements CheckBehavior {
    @Resource(name = "minerRoundService")
    private RoundService roundService;

    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Resource(name = "validatorCommitteeService")
    private ValidatorCommitteeService validatorCommitteeService;

    @Override
    public ResultView check(Map<String, Object> args) {
        String address = (String) args.get("address");

        Round round = roundService.getCurrentRound();
        boolean exist = minerOperationService.applyExist(round.getRound(), address, MinerOperationType.SignUp.getCode());
        if (exist) {
            return ResultView.error(ErrorCode.REPEAT_OPERATION_ERROR);
        }

        MinerBlockDTO minerStakeAndBlocks = validatorCommitteeService.getMinerBlocks(address);
        if (minerStakeAndBlocks.getActualBlocks() < Condition.MINER_COMMITTEE_SIGN_UP_PRODUCED_FLOOR) {
            return ResultView.error(ErrorCode.NOT_ENOUGH_PRODUCED);
        }

        return ResultView.ok();
    }
}
