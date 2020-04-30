package network.asimov.controller.foundation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.entity.foundation.BalanceSheet;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.foundation.BalanceSheetService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import network.asimov.response.foundation.BalanceSheetView;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-09-20
 */
@CrossOrigin
@RestController("foundationBalanceController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation", produces = RequestConstants.CONTENT_TYPE_JSON)
public class BalanceController {
    @Resource(name = "foundationBalanceSheetService")
    private BalanceSheetService balanceSheetService;

    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "balanceConvertBehavior")
    private ConvertBehavior<AssetDTO, AssetView> balanceConvertBehavior;

    @ApiOperation(value = "List foundation balance")
    @PostMapping(path = "/balance")
    public ResultView<List<AssetView>> balance() {
        List<AssetDTO> assetDTOList = balanceService.listBalance(ChainConstant.GENESIS_ORGANIZATION_ADDRESS);
        List<AssetView> assetViewList = balanceConvertBehavior.convert(assetDTOList);
        if (CollectionUtils.isEmpty(assetViewList)) {
            AssetView assetView = assetConvertBehavior.convert(AssetSummary.builder().asset(ChainConstant.ASSET_ASIM).value(0L).build());
            assetViewList.add(assetView);
        }

        return ResultView.ok(assetViewList);
    }

    @ApiOperation(value = "Query foundation balance sheet by page")
    @PostMapping(path = "/balancesheet", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<BalanceSheetView>> balanceSheet(@RequestBody @Validated PurePageQuery purePageQuery) {
        List<BalanceSheetView> balanceSheetViewList = new ArrayList<>();
        Pair<Long, List<BalanceSheet>> pair = balanceSheetService.listBalanceSheet(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        // 查询资产map
        Map<String, Asset> assetMap = assetService.mapAssets(pair.getRight().stream().map(BalanceSheet::getAsset).collect(Collectors.toList()));
        for (BalanceSheet balanceSheet : pair.getRight()) {
            Asset asset = assetMap.get(balanceSheet.getAsset());
            balanceSheetViewList.add(BalanceSheetView.builder()
                    .asset(asset.getAsset())
                    .address(balanceSheet.getAddress())
                    .time(balanceSheet.getTime())
                    .proposalType(balanceSheet.getProposalType())
                    .transferType(balanceSheet.getTransferType())
                    .txHash(balanceSheet.getTxHash())
                    .amount(String.valueOf(balanceSheet.getAmount()))
                    .symbol(asset != null ? asset.getSymbol() : "")
                    .build());
        }
        return ResultView.ok(PageView.of(pair.getLeft(), balanceSheetViewList));
    }
}
