package network.asimov.controller.foundation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.ErrorCode;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
import network.asimov.mysql.service.foundation.FoundationOperationService;
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
@RestController("foundationOperationController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation", produces = RequestConstants.CONTENT_TYPE_JSON)
public class OperationController {
    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @ApiOperation(value = "Get transaction status")
    @PostMapping(path = "/transaction/status")
    public ResultView<TxStatusView> getTxStatusByHash(@RequestBody @Validated TxHashRequest txHashRequest) {
        Optional<TFoundationOperation> operation = foundationOperationService.getTFoundationOperationByTxHash(txHashRequest.getTxHash());
        return operation.map(tFoundationOperation -> ResultView.ok(TxStatusView.builder().txStatus(tFoundationOperation.getTxStatus()).build()))
                .orElseGet(() -> ResultView.error(ErrorCode.DATA_NOT_EXISTS));
    }
}
