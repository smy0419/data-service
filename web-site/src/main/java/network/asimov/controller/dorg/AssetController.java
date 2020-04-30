package network.asimov.controller.dorg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.entity.dorg.OrganizationAsset;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mongodb.service.dorg.OrganizationAssetService;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.constant.DaoAsset;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TDaoAsset;
import network.asimov.mysql.database.tables.pojos.TDaoIndivisibleAsset;
import network.asimov.mysql.service.dorg.DaoAssetService;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.*;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import network.asimov.response.common.TxView;
import network.asimov.response.dorg.IssueAssetParamView;
import network.asimov.response.dorg.IssueAssetView;
import network.asimov.util.AssetUtil;
import network.asimov.util.EncodeDecodeUtil;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-12-12
 */
@CrossOrigin
@RestController("daoAssetController")
@Api(tags = {"dao"})
@RequestMapping(path = "/dao/org/asset", produces = RequestConstants.CONTENT_TYPE_JSON)
public class AssetController {
    @Resource(name = "daoOrganizationAssetService")
    private OrganizationAssetService organizationAssetService;

    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "daoAssetService")
    private DaoAssetService daoAssetService;

    @Resource(name = "issueAssetCheck")
    private CheckBehavior issueAssetCheck;

    @Resource(name = "expenseCheck")
    private CheckBehavior expenseCheck;

    @Resource(name = "mintAssetCheck")
    private CheckBehavior mintAssetCheck;

    @Resource(name = "daoSendDeletableTransaction")
    private SendTransactionBehavior daoSendDeletableTransaction;

    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "balanceConvertBehavior")
    private ConvertBehavior<AssetDTO, AssetView> balanceConvertBehavior;

    @PostMapping("/issue/check")
    @ApiOperation(value = "Check issue asset")
    public ResultView checkIssue(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                 @RequestBody @Validated IssueAssetCheckRequest issueAssetCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateAsset(address, issueAssetCheckRequest.getContractAddress(), issueAssetCheckRequest.getAssetName());
        return issueAssetCheck.check(checkArgs);
    }

    @PostMapping("/issue/prepare")
    @ApiOperation(value = "Pre-issue asset")
    public ResultView<IssueAssetParamView> prepare(@RequestBody @Validated PrepareIssueAssetRequest prepareIssueAssetRequest) {
        int assetCount = daoAssetService.countOrgAsset(prepareIssueAssetRequest.getContractAddress());
        String assetDesc = prepareIssueAssetRequest.getAssetName();
        long memberCount = memberService.countMember(prepareIssueAssetRequest.getContractAddress());

        String data = EncodeDecodeUtil.encodeCreateAsset(prepareIssueAssetRequest.getAssetName(),
                prepareIssueAssetRequest.getAssetSymbol(),
                assetDesc, prepareIssueAssetRequest.getAssetType(),
                assetCount,
                prepareIssueAssetRequest.getAmount());

        return ResultView.ok(IssueAssetParamView.builder()
                .subject("IssueAsset")
                .endTime(TimeUtil.currentSeconds() + 15 * TimeUtil.SECONDS_OF_DAY)
                .percent(67)
                .voteType(2)
                .func(data.substring(2, 10))
                .param(data.substring(10))
                .totalParticipants(memberCount)
                .build()
        );
    }

    @PostMapping("/issue")
    @ApiOperation(value = "Issue asset")
    public ResultView<TxView> issueAsset(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                         @RequestBody @Validated StartVoteRequest startVoteRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateAsset(address, startVoteRequest.getContractAddress(), startVoteRequest.getAssetName());
        ResultView check = issueAssetCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();

        int assetType = startVoteRequest.getAssetType();
        Optional<Organization> organizationOptional = organizationService.getOrganizationByAddress(startVoteRequest.getContractAddress());
        int assetCount = daoAssetService.countOrgAsset(startVoteRequest.getContractAddress());

        String assetId = AssetUtil.generateAssetId(assetType, organizationOptional.get().getOrgId(), assetCount);

        additionalInfoMap.put(OperationAdditionalKey.ASSET, assetId);
        if (StringUtils.isNotBlank(startVoteRequest.getLogo())) {
            additionalInfoMap.put(OperationAdditionalKey.ORG_LOGO, startVoteRequest.getLogo());
        }
        additionalInfoMap.put(OperationAdditionalKey.ASSET_NAME, startVoteRequest.getAssetName());
        additionalInfoMap.put(OperationAdditionalKey.ASSET_SYMBOL, startVoteRequest.getAssetSymbol());

        if (startVoteRequest.getAssetType().equals(DaoAsset.Type.Divisible.ordinal())) {
            additionalInfoMap.put(OperationAdditionalKey.ISSUE_AMOUNT, startVoteRequest.getAmountOrVoucherId());
        } else {
            additionalInfoMap.put(OperationAdditionalKey.VOUCHER_ID, startVoteRequest.getAmountOrVoucherId());
            additionalInfoMap.put(OperationAdditionalKey.ASSET_DESC, startVoteRequest.getAssetDesc());
        }

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.IssueAsset.getCode(), additionalInfoMap, address, startVoteRequest.getContractAddress());
        ResultView<TxView> resultView = daoSendDeletableTransaction.perform(startVoteRequest.getCallData(), args);

        TDaoAsset asset = new TDaoAsset();
        asset.setContractAddress(startVoteRequest.getContractAddress());
        asset.setAsset(assetId);
        asset.setLogo(startVoteRequest.getLogo());
        asset.setName(startVoteRequest.getAssetName());
        asset.setSymbol(startVoteRequest.getAssetSymbol());
        asset.setTxHash(resultView.getData().getTxHash());
        daoAssetService.saveAsset(asset);

        if (assetType == DaoAsset.Type.Indivisible.ordinal()) {
            TDaoIndivisibleAsset indivisibleAsset = new TDaoIndivisibleAsset();
            indivisibleAsset.setTxHash(resultView.getData().getTxHash());
            indivisibleAsset.setContractAddress(startVoteRequest.getContractAddress());
            indivisibleAsset.setAsset(assetId);
            indivisibleAsset.setVoucherId(startVoteRequest.getAmountOrVoucherId());
            indivisibleAsset.setAssetDesc(startVoteRequest.getAssetDesc());
            daoIndivisibleAssetService.saveIndivisibleAsset(indivisibleAsset);
        }
        return resultView;
    }

    @PostMapping("/expense/check")
    @ApiOperation(value = "Check expense")
    public ResultView checkExpense(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                   @RequestBody @Validated ExpenseCheckRequest expenseCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateExpense(address, expenseCheckRequest.getContractAddress(), expenseCheckRequest.getAsset(), expenseCheckRequest.getAssetType(), expenseCheckRequest.getAmount());
        return expenseCheck.check(checkArgs);
    }

    @PostMapping("/expense")
    @ApiOperation(value = "Expense")
    public ResultView expenseAsset(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                   @RequestBody @Validated ExpenseRequest expenseRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateExpense(address, expenseRequest.getContractAddress(), expenseRequest.getAsset(), expenseRequest.getAssetType(), expenseRequest.getAmount());
        ResultView check = expenseCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Optional<Asset> assetOptional = assetService.getAsset(expenseRequest.getAsset());

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.TARGET_ADDRESS, expenseRequest.getTargetAddress());
        additionalInfoMap.put(OperationAdditionalKey.INVEST_ASSET, expenseRequest.getAsset());
        additionalInfoMap.put(OperationAdditionalKey.ASSET_NAME, assetOptional.get().getName());
        additionalInfoMap.put(OperationAdditionalKey.INVEST_AMOUNT, expenseRequest.getAmount());

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.TransferAsset.getCode(), additionalInfoMap, address, expenseRequest.getContractAddress());
        return daoSendDeletableTransaction.perform(expenseRequest.getCallData(), args);
    }

    @PostMapping("/list")
    @ApiOperation(value = "List asset")
    public ResultView<List<AssetView>> listAsset(@RequestBody @Validated ContractAddressQuery contractAddressQuery) {
        List<AssetDTO> assetDTOList = balanceService.listBalance(contractAddressQuery.getContractAddress());
        List<AssetView> assetViewList = balanceConvertBehavior.convert(assetDTOList);
        return ResultView.ok(assetViewList);
    }

    @PostMapping("/issue/list")
    @ApiOperation(value = "List issued asset")
    public ResultView<List<IssueAssetView>> listIssueAsset(@RequestBody @Validated ContractAddressQuery contractAddressQuery) {
        List<IssueAssetView> issueAssetView = Lists.newArrayList();
        List<OrganizationAsset> orgAssetList = organizationAssetService.listOrgAssetByAddress(contractAddressQuery.getContractAddress());
        Map<String, Asset> assetMap = assetService.mapAssets(orgAssetList.stream().map(OrganizationAsset::getAsset).collect(Collectors.toList()));

        for (OrganizationAsset orgAsset : orgAssetList) {
            Asset asset = assetMap.get(orgAsset.getAsset());
            issueAssetView.add(IssueAssetView.builder()
                    .asset(orgAsset.getAsset())
                    .assetIndex(orgAsset.getAssetIndex())
                    .assetType(orgAsset.getAssetType())
                    .name(asset.getName())
                    .symbol(asset.getSymbol())
                    .build());
        }
        return ResultView.ok(issueAssetView);
    }

    @PostMapping("/mint/check")
    @ApiOperation(value = "Check mint asset")
    public ResultView checkMintAsset(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                     @RequestBody @Validated MintAssetCheckRequest mintAssetCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateMintAsset(address, mintAssetCheckRequest.getContractAddress(), mintAssetCheckRequest.getAsset(), mintAssetCheckRequest.getAssetType(), mintAssetCheckRequest.getAmountOrVoucherId());
        return mintAssetCheck.check(checkArgs);
    }

    @PostMapping("/mint")
    @ApiOperation(value = "Mint asset")
    public ResultView<TxView> mintAsset(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                        @RequestBody @Validated MintAssetRequest mintAssetRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateMintAsset(address, mintAssetRequest.getContractAddress(), mintAssetRequest.getAsset(), mintAssetRequest.getAssetType(), mintAssetRequest.getAmountOrVoucherId());
        ResultView check = mintAssetCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.ASSET, mintAssetRequest.getAsset());
        if (mintAssetRequest.getAssetType().equals(DaoAsset.Type.Divisible.ordinal())) {
            additionalInfoMap.put(OperationAdditionalKey.MINT_AMOUNT, mintAssetRequest.getAmountOrVoucherId());
        } else {
            additionalInfoMap.put(OperationAdditionalKey.VOUCHER_ID, mintAssetRequest.getAmountOrVoucherId());
            additionalInfoMap.put(OperationAdditionalKey.ASSET_DESC, mintAssetRequest.getAssetDesc());
        }

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.MintAsset.getCode(), additionalInfoMap, address, mintAssetRequest.getContractAddress());
        ResultView<TxView> resultView = daoSendDeletableTransaction.perform(mintAssetRequest.getCallData(), args);

        if (mintAssetRequest.getAssetType().equals(DaoAsset.Type.Indivisible.ordinal())) {
            TDaoIndivisibleAsset indivisibleAsset = new TDaoIndivisibleAsset();
            indivisibleAsset.setTxHash(resultView.getData().getTxHash());
            indivisibleAsset.setContractAddress(mintAssetRequest.getContractAddress());
            indivisibleAsset.setAsset(mintAssetRequest.getAsset());
            indivisibleAsset.setVoucherId(mintAssetRequest.getAmountOrVoucherId());
            indivisibleAsset.setAssetDesc(mintAssetRequest.getAssetDesc());
            daoIndivisibleAssetService.saveIndivisibleAsset(indivisibleAsset);
        }
        return resultView;
    }

}
