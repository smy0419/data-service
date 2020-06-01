package network.asimov.controller.ascan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.ascan.AssetIssue;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.*;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.redis.service.TransactionCacheService;
import network.asimov.request.RequestConstants;
import network.asimov.request.ascan.AssetNamePageQuery;
import network.asimov.request.ascan.AssetPageQuery;
import network.asimov.request.ascan.AssetQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.ascan.AssetDetailView;
import network.asimov.response.ascan.AssetIssueView;
import network.asimov.response.ascan.AssetSummaryView;
import network.asimov.response.ascan.TransactionView;
import network.asimov.response.common.AssetView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-10-31
 */
@CrossOrigin
@RestController("assetController")
@Api(tags = "ascan")
@RequestMapping(path = "/ascan/asset", produces = RequestConstants.CONTENT_TYPE_JSON)
public class AssetController {
    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "assetIssueService")
    private AssetIssueService assetIssueService;

    @Resource(name = "addressAssetBalanceService")
    private AddressAssetBalanceService addressAssetBalanceService;

    @Resource(name = "tradingService")
    private TradingService tradingService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    @Resource(name = "transactionStatisticsService")
    private TransactionStatisticsService transactionStatisticsService;

    @Resource(name = "transactionCacheService")
    private TransactionCacheService transactionCacheService;

    @ApiOperation(value = "Get asset detail information")
    @PostMapping("/detail")
    public ResultView<AssetDetailView> getAssetDetailInfo(@RequestBody @Validated AssetQuery assetQuery) {
        Optional<Asset> assetOptional = assetService.getAsset(assetQuery.getAsset());
        if (!assetOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }

        long amount = assetIssueService.sumAssetIssue(assetQuery.getAsset());

        long count =  transactionStatisticsService.count(assetQuery.getAsset());

        long holderCounts = addressAssetBalanceService.countHoldersByAsset(assetQuery.getAsset());

        long tradingVolume = tradingService.tradingVolume(assetQuery.getAsset());

        AssetDetailView assetDetailView = AssetDetailView.builder()
                .asset(AssetView.builder()
                        .asset(assetQuery.getAsset())
                        .logo(assetOptional.get().getLogo())
                        .name(assetOptional.get().getName())
                        .build())
                .transactionCount(count)
                .holderCounts(holderCounts)
                .amount(String.valueOf(amount))
                .issueAddress(assetOptional.get().getIssueAddress())
                .issueTime(assetOptional.get().getTime())
                .transactionVolume(String.valueOf(tradingVolume))
                .marketCap("")
                .price(0D)
                .build();
        return ResultView.ok(assetDetailView);
    }

    @ApiOperation(value = "Get transactions via asset")
    @PostMapping("/transaction/list")
    public ResultView<PageView<TransactionView>> listTransactionByAsset(@RequestBody @Validated AssetPageQuery assetPageQuery) {
        Pair<Long, List<String>> transactions = transactionCacheService.queryTransactionCache(assetPageQuery.getAsset(), assetPageQuery.getPage().getIndex(), assetPageQuery.getPage().getLimit());
        List<TransactionView> transactionViews = transactions.getRight().stream().map(v -> {
            JSONObject tx = JSONObject.parseObject(v);
            JSONArray feeArray = tx.getJSONArray("fee");
            List<AssetSummary> feeList = Lists.newArrayList();
            for(int i = 0; i < feeArray.size(); i++) {
                JSONObject fee = feeArray.getJSONObject(i);
                feeList.add(AssetSummary.builder().asset(fee.getString("asset")).value(fee.getLongValue("value")).build());
            }

            return TransactionView.builder()
                    .hash(tx.getString("tx_hash"))
                    .time(tx.getLong("time"))
                    .fee(assetConvertBehavior.convert(feeList)).build();
        }).collect(Collectors.toList());
        return ResultView.ok(PageView.of(transactions.getLeft(), transactionViews));
    }

    @ApiOperation(value = "Get summary information via asset")
    @PostMapping("/summary")
    public ResultView<PageView<AssetSummaryView>> getAssetSummary(@RequestBody @Validated AssetNamePageQuery assetNamePageQuery) {
        Pair<Long, List<Asset>> assetPair = assetService.listAssetByPageAndName(assetNamePageQuery.getPage().getIndex(), assetNamePageQuery.getPage().getLimit(), assetNamePageQuery.getAssetName());
        List<AssetSummaryView> assetSummaryViewList = Lists.newArrayList();
        for (Asset asset : assetPair.getRight()) {
            long tradingVolume = tradingService.tradingVolume(asset.getAsset());
            long amount = assetIssueService.sumAssetIssue(asset.getAsset());
            assetSummaryViewList.add(AssetSummaryView.builder()
                    .asset(AssetView.builder()
                            .asset(asset.getAsset())
                            .name(asset.getName())
                            .logo(asset.getLogo())
                            .build())
                    .transactionVolume(String.valueOf(tradingVolume))
                    .amount(String.valueOf(amount))
                    .time(asset.getTime())
                    .build());
        }

        return ResultView.ok(PageView.of(assetPair.getLeft(), assetSummaryViewList));
    }

    @ApiOperation(value = "Get issue list via asset")
    @PostMapping("/issue/list")
    public ResultView<PageView<AssetIssueView>> listIssueByAsset(@RequestBody @Validated AssetPageQuery assetPageQuery) {
        Pair<Long, List<AssetIssue>> assetIssuePair = assetIssueService.queryAssetIssue(assetPageQuery.getPage().getIndex(), assetPageQuery.getPage().getLimit(), assetPageQuery.getAsset());

        List<Long> voucherIds = assetIssuePair.getRight().stream().map(AssetIssue::getValue).collect(Collectors.toList());
        Map<Long, String> indivisibleDesc = daoIndivisibleAssetService.mapIndivisibleDesc(assetPageQuery.getAsset(), voucherIds);
        List<AssetIssueView> assetIssueViews = assetIssuePair.getRight().stream().map(v -> {
            String desc = StringUtils.EMPTY;
            if (indivisibleDesc.containsKey(v.getValue())) {
                desc = indivisibleDesc.get(v.getValue());
            }
            return AssetIssueView.builder()
                    .time(v.getTime())
                    .value(String.valueOf(v.getValue()))
                    .description(desc).build();
        }).collect(Collectors.toList());
        return ResultView.ok(PageView.of(assetIssuePair.getLeft(), assetIssueViews));
    }
}
