package network.asimov.controller.ascan;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.TransactionStatisticsService;
import network.asimov.redis.service.TransactionCacheService;
import network.asimov.request.RequestConstants;
import network.asimov.request.ascan.AddressPageQuery;
import network.asimov.request.ascan.AddressQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.ascan.AddressBalanceView;
import network.asimov.response.ascan.AddressDetailView;
import network.asimov.response.ascan.AddressTransactionView;
import network.asimov.response.common.AssetView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-11-04
 */

@CrossOrigin
@RestController("addressController")
@Api(tags = "ascan")
@RequestMapping(path = "/ascan/address", produces = RequestConstants.CONTENT_TYPE_JSON)
public class AddressController {
    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "balanceConvertBehavior")
    private ConvertBehavior<AssetDTO, AssetView> balanceConvertBehavior;

    @Resource(name = "transactionStatisticsService")
    private TransactionStatisticsService transactionStatisticsService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "addressExistCheck")
    private CheckBehavior addressExistCheck;

    @Resource(name = "transactionCacheService")
    private TransactionCacheService transactionCacheService;

    @ApiOperation(value = "Get detail information via address")
    @PostMapping(path = "/detail")
    public ResultView<AddressDetailView> getAddressDetail(@RequestBody @Validated AddressQuery addressQuery) {
        Map<String, Object> args = CheckArgsUtil.generateKey(addressQuery.getAddress());
        ResultView checkResult = addressExistCheck.check(args);
        if (!checkResult.success()) {
            return checkResult;
        }


        Optional<AssetDTO> asimov = balanceService.getAsimov(addressQuery.getAddress());
        AssetView assetView = null;
        if (asimov.isPresent()) {
            assetView = balanceConvertBehavior.convert(asimov.get());
        }

        long txCount = transactionStatisticsService.count(addressQuery.getAddress());

        return ResultView.ok(
                AddressDetailView.builder()
                        .address(addressQuery.getAddress())
                        .txCount(txCount)
                        .asim(assetView)
                        .build()
        );
    }

    @ApiOperation(value = "Get transaction via address")
    @PostMapping(path = "/transaction/query")
    public ResultView<PageView<AddressTransactionView>> listAddressTransaction(@RequestBody @Validated AddressPageQuery addressPageQuery) {
        Pair<Long, List<String>> transactions = transactionCacheService.queryTransactionCache(addressPageQuery.getAddress(), addressPageQuery.getPage().getIndex(), addressPageQuery.getPage().getLimit());
        List<AddressTransactionView> transactionViews = transactions.getRight().stream().map(v -> {
            JSONObject tx = JSONObject.parseObject(v);
            JSONArray feeArray = tx.getJSONArray("fee");
            List<AssetSummary> feeList = Lists.newArrayList();
            for(int i = 0; i < feeArray.size(); i++) {
                JSONObject fee = feeArray.getJSONObject(i);
                feeList.add(AssetSummary.builder().asset(fee.getString("asset")).value(fee.getLongValue("value")).build());
            }

            return AddressTransactionView.builder()
                    .txHash(tx.getString("tx_hash"))
                    .time(tx.getLong("time"))
                    .fee(assetConvertBehavior.convert(feeList)).build();
        }).collect(Collectors.toList());
        return ResultView.ok(PageView.of(transactions.getLeft(), transactionViews));
    }

    @ApiOperation(value = "Get balances via address")
    @PostMapping(path = "/balances")
    public ResultView<AddressBalanceView> listBalances(@RequestBody @Validated AddressQuery addressQuery) {
        Map<String, Object> args = CheckArgsUtil.generateKey(addressQuery.getAddress());
        ResultView checkResult = addressExistCheck.check(args);
        if (!checkResult.success()) {
            return checkResult;
        }

        List<AssetDTO> assetDTOList = balanceService.listBalanceFromDatabase(addressQuery.getAddress());
        List<AssetView> balances = balanceConvertBehavior.convert(assetDTOList);
        return ResultView.ok(
                AddressBalanceView.builder()
                        .balances(balances)
                        .build()
        );
    }
}
