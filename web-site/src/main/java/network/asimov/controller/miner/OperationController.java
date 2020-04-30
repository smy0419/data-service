package network.asimov.controller.miner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.ErrorCode;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.TxHashRequest;
import network.asimov.response.ResultView;
import network.asimov.response.common.TxStatusView;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-10-29
 */
@CrossOrigin
@RestController("minerOperationController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class OperationController {
    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @ApiOperation(value = "Get transaction status")
    @PostMapping(path = "/transaction/status")
    public ResultView<TxStatusView> getTxStatusByHash(@RequestBody @Validated TxHashRequest txHashRequest) {
        Optional<TMinerOperation> operation = minerOperationService.getTMinerOperationByTxHash(txHashRequest.getTxHash());
        if (!operation.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        return ResultView.ok(TxStatusView.builder().txStatus(operation.get().getTxStatus()).build());
    }
}
