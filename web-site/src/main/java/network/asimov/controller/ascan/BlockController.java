package network.asimov.controller.ascan;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.behavior.convert.RawTxAssembleBehavior;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.Block;
import network.asimov.mongodb.entity.ascan.Transaction;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.BlockService;
import network.asimov.mongodb.service.ascan.TransactionService;
import network.asimov.request.PageQuery;
import network.asimov.request.RequestConstants;
import network.asimov.request.ascan.BlockDetailRequest;
import network.asimov.request.ascan.BlockTransactionQuery;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.ascan.BlockDetailView;
import network.asimov.response.ascan.BlockHeightView;
import network.asimov.response.ascan.BlockView;
import network.asimov.response.ascan.TransactionView;
import network.asimov.response.common.AssetView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-10-28
 */

@CrossOrigin
@RestController("blockController")
@Api(tags = "ascan")
@RequestMapping(path = "/ascan", produces = RequestConstants.CONTENT_TYPE_JSON)
public class BlockController {

    @Resource(name = "blockService")
    private BlockService blockService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> convertBehavior;

    @Resource(name = "transactionService")
    private TransactionService transactionService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "rawTxAssembleBehavior")
    private RawTxAssembleBehavior rawTxAssembleBehavior;

    @ApiOperation(value = "Query block information by page")
    @PostMapping(path = "/block/query")
    public ResultView<PageView<BlockView>> queryBlock(@RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<Block>> blocks = blockService.queryBlockByPage(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<BlockView> blockViews = blocks.getRight().stream().map(v -> BlockView.builder()
                .height(v.getHeight())
                .transactions(v.getTxCount())
                .time(v.getTime())
                .produced(v.getProduced())
                .reward(convertBehavior.convert(AssetSummary.builder().asset(ChainConstant.ASSET_ASIM).value(v.getReward()).build()))
                .fee(convertBehavior.convert(v.getFee())).build()).collect(Collectors.toList());

        return ResultView.ok(PageView.of(blocks.getLeft(), blockViews));
    }

    @ApiOperation(value = "Get current block height")
    @PostMapping(path = "/block/height")
    public ResultView<BlockHeightView> getHandledBlockHeight() {
        Optional<Long> height = blockService.getHandledBlockHeight();
        BlockHeightView blockHeightView = BlockHeightView.builder().height(0).build();
        height.ifPresent(v -> blockHeightView.setHeight(v+1));

        return ResultView.ok(blockHeightView);
    }

    @ApiOperation(value = "Get block detail information")
    @PostMapping(path = "/block/detail")
    public ResultView<BlockDetailView> blockDetail(@RequestBody BlockDetailRequest blockDetailRequest) {
        if (blockDetailRequest.getHeight() == null && StringUtils.isBlank(blockDetailRequest.getHash())) {
            return ResultView.error(ErrorCode.PARAMETER_INVALID.getCode(),
                    String.format(ErrorCode.PARAMETER_INVALID.getMsg(), "height or hash not null"));
        }

        // Get block information
        Optional<Block> blockOptional = blockDetailRequest.getHeight() != null ?
                blockService.getBlock(blockDetailRequest.getHeight()) :
                blockService.getBlock(blockDetailRequest.getHash());
        if (!blockOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        Block block = blockOptional.get();

        // Get transaction list
        if (blockDetailRequest.getPage() == null) {
            PageQuery pageQuery = new PageQuery();
            pageQuery.setIndex(1);
            pageQuery.setLimit(10);
            blockDetailRequest.setPage(pageQuery);
        }
        Pair<Long, List<Transaction>> pair = transactionService.listTransaction(block.getHeight(), blockDetailRequest.getPage().getIndex(), blockDetailRequest.getPage().getLimit());

        return ResultView.ok(assembleBlockDetail(block, pair));
    }

    @ApiOperation(value = "Get block transaction list")
    @PostMapping(path = "/block/transaction/list")
    public ResultView<PageView<TransactionView>> listBlockTransaction(@RequestBody @Validated BlockTransactionQuery blockTransactionQuery) {
        if (blockTransactionQuery.getHeight() == null && StringUtils.isBlank(blockTransactionQuery.getHash())) {
            return ResultView.error(ErrorCode.PARAMETER_INVALID.getCode(),
                    String.format(ErrorCode.PARAMETER_INVALID.getMsg(), "height or hash not null"));
        }

        // Get block information
        Optional<Block> blockOptional = blockTransactionQuery.getHeight() != null ?
                blockService.getBlock(blockTransactionQuery.getHeight()) :
                blockService.getBlock(blockTransactionQuery.getHash());
        if (!blockOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        Block block = blockOptional.get();

        Pair<Long, List<Transaction>> pair = transactionService.listTransaction(block.getHeight(), blockTransactionQuery.getPage().getIndex(), blockTransactionQuery.getPage().getLimit());
        List<TransactionView> transactionViewList = Lists.newArrayList();
        for (Transaction transaction : pair.getRight()) {
            transactionViewList.add(TransactionView.builder()
                    .time(transaction.getTime())
                    .hash(transaction.getHash())
                    .fee(assetConvertBehavior.convert(transaction.getFee()))
                    .build());
        }
        PageView pageView = PageView.of(pair.getLeft(), transactionViewList);
        return ResultView.ok(pageView);
    }


    private BlockDetailView assembleBlockDetail(Block block, Pair<Long, List<Transaction>> pair) {
        BlockDetailView blockDetailView = BlockDetailView.builder()
                .hash(block.getHash())
                .confirmations(block.getConfirmations())
                .size(block.getSize())
                .height(block.getHeight())
                .time(block.getTime())
                .txCount(block.getTxCount())
                .produced(block.getProduced())
                .reward(convertBehavior.convert(AssetSummary.builder().value(block.getReward()).asset(ChainConstant.ASSET_ASIM).build()))
                .fee(convertBehavior.convert(block.getFee()))
                .round(block.getRound())
                .slot(block.getSlot())
                .build();

        List<TransactionView> transactionViewList = Lists.newArrayList();
        for (Transaction tx : pair.getRight()) {
            TransactionView transactionView = TransactionView.builder()
                    .time(tx.getTime())
                    .hash(tx.getHash())
                    .fee(assetConvertBehavior.convert(tx.getFee()))
                    .build();
            transactionViewList.add(transactionView);
        }
        PageView txView = PageView.of(pair.getLeft(), transactionViewList);
        blockDetailView.setTx(txView);
        return blockDetailView;
    }
}
