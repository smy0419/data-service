package network.asimov.controller.ascan;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.pojo.ContractSourceDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.chainrpc.service.ContractService;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.ContractTransaction;
import network.asimov.mongodb.entity.ascan.TransactionCount;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.service.ascan.TransactionStatisticsService;
import network.asimov.request.RequestConstants;
import network.asimov.request.ascan.AddressPageQuery;
import network.asimov.request.ascan.AddressQuery;
import network.asimov.request.ascan.ContractSourceQuery;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.ascan.*;
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
@RestController("contractController")
@Api(tags = "ascan")
@RequestMapping(path = "/ascan/contract", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ContractController {
    @Resource(name = "addressExistCheck")
    private CheckBehavior addressExistCheck;

    @Resource(name = "transactionStatisticsService")
    private TransactionStatisticsService transactionStatisticsService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "balanceConvertBehavior")
    private ConvertBehavior<AssetDTO, AssetView> balanceConvertBehavior;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "contractService")
    private ContractService contractService;

    @ApiOperation(value = "Get contract detail information via address")
    @PostMapping(path = "/detail")
    public ResultView<ContractDetailView> getContractDetail(@RequestBody @Validated AddressQuery addressQuery) {
        Map<String, Object> args = CheckArgsUtil.generateKey(addressQuery.getAddress());
        ResultView checkResult = addressExistCheck.check(args);
        if (!checkResult.success()) {
            return checkResult;
        }
        TransactionCount contract = (TransactionCount) checkResult.getData();

        Optional<AssetDTO> asimov = balanceService.getAsimov(contract.getKey());
        AssetView assetView = null;
        if (asimov.isPresent()) {
            assetView = balanceConvertBehavior.convert(asimov.get());
        }

        return ResultView.ok(
                ContractDetailView.builder()
                        .address(contract.getKey())
                        .txHash(contract.getTxHash())
                        .txCount(contract.getTxCount())
                        .time(contract.getTime())
                        .creator(contract.getCreator())
                        .templateType(contract.getTemplateType())
                        .templateName(contract.getTemplateTame())
                        .asim(assetView)
                        .build()
        );
    }

    @ApiOperation(value = "Query transaction by page via address")
    @PostMapping(path = "/transaction/query")
    public ResultView<PageView<AddressTransactionView>> listContractTransaction(@RequestBody @Validated AddressPageQuery addressPageQuery) {
        Pair<Long, List<ContractTransaction>> transactions =  transactionStatisticsService.queryTxListByPage(ContractTransaction.class, addressPageQuery.getAddress(), addressPageQuery.getPage().getIndex(), addressPageQuery.getPage().getLimit());
        List<AddressTransactionView> transactionViews = transactions.getRight()
                .stream().map(v -> AddressTransactionView.builder()
                        .txHash(v.getTxHash())
                        .time(v.getTime())
                        .fee(assetConvertBehavior.convert(v.getFee())).build()).collect(Collectors.toList());

        return ResultView.ok(PageView.of(transactions.getLeft(), transactionViews));
    }


    @ApiOperation(value = "Query contract detail information")
    @PostMapping(path = "/query")
    public ResultView<PageView<ContractView>> listContract(@RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<TransactionCount>> contracts =  transactionStatisticsService.queryDataByPage(TransactionCount.TxCountCategory.Contract,purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());

        List<ContractView> contractViews = contracts.getRight()
                .stream().map(v -> ContractView.builder()
                        .address(v.getKey())
                        .time(v.getTime())
                        .txCount(v.getTxCount())
                        .creator(v.getCreator()).build()).collect(Collectors.toList());

        return ResultView.ok(PageView.of(contracts.getLeft(), contractViews));
    }

    @ApiOperation(value = "Get contract source information")
    @PostMapping(path = "/info")
    public ResultView<ContractSourceView> getContractSource(@RequestBody @Validated ContractSourceQuery contractSourceQuery) {
        Optional<ContractSourceDTO> contractSourceDtoOptional = contractService.getSource(contractSourceQuery.getCategory(), contractSourceQuery.getName());
        if (!contractSourceDtoOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }

        ContractSourceDTO contractSourceDto = contractSourceDtoOptional.get();
        ContractSourceView contractSourceView = ContractSourceView.builder()
                .category(contractSourceDto.getCategory())
                .templateName(contractSourceDto.getTemplateName())
                .abi(contractSourceDto.getAbi())
                .byteCode(contractSourceDto.getByteCode())
                .source(contractSourceDto.getSource())
                .build();

        return ResultView.ok(contractSourceView);
    }

    @ApiOperation(value = "Get balance via contract address")
    @PostMapping(path = "/balances")
    public ResultView<AddressBalanceView> listBalances(@RequestBody @Validated AddressQuery addressQuery) {
        Map<String, Object> args = CheckArgsUtil.generateKey(addressQuery.getAddress());
        ResultView checkResult = addressExistCheck.check(args);
        if (!checkResult.success()) {
            return checkResult;
        }

        List<AssetDTO> assetDTOList = balanceService.listBalance(addressQuery.getAddress());
        List<AssetView> balances = balanceConvertBehavior.convert(assetDTOList);
        return ResultView.ok(
                AddressBalanceView.builder()
                        .balances(balances)
                        .build()
        );
    }
}
