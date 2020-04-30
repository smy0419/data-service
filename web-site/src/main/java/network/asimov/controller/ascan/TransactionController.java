package network.asimov.controller.ascan;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.behavior.convert.RawTxAssembleBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.Transaction;
import network.asimov.mongodb.entity.ascan.Vin;
import network.asimov.mongodb.entity.ascan.Vout;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.TransactionService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.HashRequest;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.ascan.RawTxView;
import network.asimov.response.ascan.TransactionCountView;
import network.asimov.response.ascan.TransactionView;
import network.asimov.response.common.AssetView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-10-31
 */
@CrossOrigin
@RestController("transactionController")
@Api(tags = "ascan")
@RequestMapping(path = "/ascan", produces = RequestConstants.CONTENT_TYPE_JSON)
public class TransactionController {
    @Resource(name = "transactionService")
    private TransactionService transactionService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "rawTxAssembleBehavior")
    private RawTxAssembleBehavior rawTxAssembleBehavior;

    @ApiOperation(value = "Query block information by page")
    @PostMapping(path = "/transaction/query")
    public ResultView<PageView<TransactionView>> queryTransaction(@RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<Transaction>> transactions = transactionService.queryBlockByPage(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<TransactionView> transactionViews = transactions.getRight().stream().map(v -> TransactionView.builder()
                .hash(v.getHash())
                .time(v.getTime())
                .fee(assetConvertBehavior.convert(v.getFee())).build()).collect(Collectors.toList());

        return ResultView.ok(PageView.of(transactions.getLeft(), transactionViews));
    }

    @ApiOperation(value = "Get transaction via hash")
    @PostMapping(path = "/transaction/detail")
    public ResultView<RawTxView> transactionDetail(@RequestBody @Validated HashRequest hashRequest) {
        Optional<Transaction> transactionOptional = transactionService.getByHash(hashRequest.getHash());
        if (!transactionOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        Transaction tx = transactionOptional.get();

        List<Vin> vins = tx.getVin();

        List<Vout> vouts = tx.getVout();

        return ResultView.ok(assembleTransaction(tx, vins, vouts));
    }

    @ApiOperation(value = "Get transaction count")
    @PostMapping(path = "/transaction/count")
    public ResultView<TransactionCountView> getTransactionCount() {
        long txCount = transactionService.count();
        return ResultView.ok(TransactionCountView.builder().txCount(txCount).build());
    }

    private RawTxView assembleTransaction(Transaction tx, List<Vin> vins, List<Vout> vouts) {
        RawTxView rawTxView = RawTxView.builder()
                .hash(tx.getHash())
                .vtxHash(tx.getVtxHash())
                .size(tx.getSize())
                .time(tx.getTime())
                .fee(assetConvertBehavior.convert(tx.getFee()))
                .height(tx.getHeight())
                .blockHash(tx.getBlockHash())
                .confirmations(tx.getConfirmations())
                .gasLimit(tx.getGasLimit())
                .build();

        rawTxAssembleBehavior.setVinVout(tx, rawTxView, vins, vouts);

        return rawTxView;
    }
}
