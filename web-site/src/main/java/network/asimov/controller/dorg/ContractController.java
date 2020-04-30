package network.asimov.controller.dorg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.chainrpc.service.contract.DaoContractService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.ContractTypeQuery;
import network.asimov.response.ResultView;
import network.asimov.response.common.ContractView;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2019-10-15
 */

@CrossOrigin
@RestController("daoContractController")
@Api(tags = "dao")
@RequestMapping(path = "/dao", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ContractController {
    @Resource(name = "daoContractService")
    private DaoContractService daoContractService;

    @ApiOperation(value = "Get dao contract information")
    @PostMapping(path = "/contract")
    public ResultView<ContractView> getMinerContract(@RequestBody @Validated ContractTypeQuery contractTypeQuery) {
        String abi = daoContractService.getAbi(contractTypeQuery.getContractType());
        ContractView contractView = ContractView.builder().abi(abi).build();
        return ResultView.ok(contractView);
    }
}
