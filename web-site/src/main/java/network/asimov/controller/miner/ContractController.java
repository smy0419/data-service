package network.asimov.controller.miner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.chainrpc.service.contract.SystemContractService;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.common.ContractView;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2019-10-08
 */

@CrossOrigin
@RestController("minerContractController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ContractController {
    @Resource(name = "systemContractService")
    private SystemContractService systemContractService;

    @ApiOperation(value = "Get miner contract information")
    @PostMapping(path = "/contract")
    public ResultView<ContractView> getMinerContract() {
        String abi = systemContractService.getAbi(ChainConstant.VALIDATOR_COMMITTEE_ADDRESS);
        ContractView contractView = ContractView.builder().abi(abi).address(ChainConstant.VALIDATOR_COMMITTEE_ADDRESS).build();
        return ResultView.ok(contractView);
    }
}
