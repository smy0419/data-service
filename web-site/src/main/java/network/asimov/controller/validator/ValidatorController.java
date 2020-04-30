package network.asimov.controller.validator;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.convert.ConvertBehavior;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.service.BalanceService;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.GroupSum;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.common.AssetSummary;
import network.asimov.mongodb.entity.validator.*;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.validator.BtcMinerService;
import network.asimov.mongodb.service.validator.EarningService;
import network.asimov.mongodb.service.validator.ValidatorRelationService;
import network.asimov.mongodb.service.validator.ValidatorService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.validator.AddressPageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import network.asimov.response.validator.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
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
 * @date 2019-11-14
 */
@CrossOrigin
@RestController("validationValidatorController")
@Api(tags = "validation")
@RequestMapping(path = "/validation/validator", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ValidatorController {

    @Resource(name = "validatorService")
    private ValidatorService validatorService;

    @Resource(name = "validatorEarningService")
    private EarningService earningService;

    @Resource(name = "balanceService")
    private BalanceService balanceService;

    @Resource(name = "assetConvertBehavior")
    private ConvertBehavior<AssetSummary, AssetView> assetConvertBehavior;

    @Resource(name = "balanceConvertBehavior")
    private ConvertBehavior<AssetDTO, AssetView> balanceConvertBehavior;

    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "validatorRelationService")
    private ValidatorRelationService validatorRelationService;

    @Resource(name = "btcMinerService")
    private BtcMinerService btcMinerService;

    @ApiOperation(value = "Query validator's role")
    @PostMapping("/role")
    public ResultView<RoleView> getValidatorRole(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        Optional<Validator> validatorOptional = validatorService.getByAddress(address);
        if (validatorOptional.isPresent()) {
            return ResultView.ok(RoleView.builder()
                    .role(Validator.Role.Validator.ordinal())
                    .build());
        }
        return ResultView.ok(RoleView.builder()
                .role(Validator.Role.Nothing.ordinal())
                .build());
    }

    @ApiOperation(value = "Count validators")
    @PostMapping("/number")
    public ResultView<NumberView> getValidatorNumber() {
        long number = validatorService.countValidators();
        return ResultView.ok(NumberView.builder().number(number).build());
    }

    @ApiOperation(value = "Get my earning")
    @PostMapping("/earning/mine")
    public ResultView<MyEarningView> getMyEarning(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        List<EarningAssetView> earningAssetViewList = Lists.newArrayList();
        List<GroupSum> myGroupSum = earningService.listEarningByAddress(address);

        List<String> assetIds = myGroupSum.stream().map(GroupSum::get_id).collect(Collectors.toList());
        Map<String, Asset> assetMap = assetService.mapAssets(assetIds);

        for (GroupSum groupSum : myGroupSum) {
            Asset asset = assetMap.get(groupSum.get_id());
            earningAssetViewList.add(EarningAssetView.builder()
                    .asset(groupSum.get_id())
                    .name(asset.getName())
                    .symbol(asset.getSymbol())
                    .logo(asset.getLogo())
                    .value(String.valueOf(groupSum.getValue()))
                    .build());
        }

        List<ValidatorRelation> relationList = validatorRelationService.listValidatorRelationByAddress(address);
        List<String> addressList = relationList.stream().map(ValidatorRelation::getBtcMinerAddress).collect(Collectors.toList());
        List<BtcMiner> btcMinerList = btcMinerService.listBtcMinerByAddress(addressList);
        List<BtcMinerView> btcMinerViewList = Lists.newArrayList();

        long validatedBlocks = earningService.countEarningByAddress(address);
        for (BtcMiner btcMiner : btcMinerList) {
            btcMinerViewList.add(BtcMinerView.builder()
                    .address(btcMiner.getAddress())
                    .domain(btcMiner.getDomain())
                    .build());
        }

        return ResultView.ok(MyEarningView.builder()
                .totalEarning(earningAssetViewList)
                .validatedBlocks(validatedBlocks)
                .btcMiner(btcMinerViewList)
                .build());
    }

    @ApiOperation(value = "List validator")
    @PostMapping(path = "/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ValidatorView>> listValidator(@RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<Validator>> pair = validatorService.listValidatorByPage(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<ValidatorView> validatorViewList = Lists.newArrayList();
        for (Validator v : pair.getRight()) {
            validatorViewList.add(ValidatorView.builder()
                    .address(v.getAddress())
                    .plannedBlocks(v.getPlannedBlocks())
                    .actualBlocks(v.getActualBlocks())
                    .build());
        }

        PageView pageView = PageView.of(pair.getLeft(), validatorViewList);
        return ResultView.ok(pageView);

    }

    @ApiOperation(value = "Query validator's location")
    @PostMapping("/location")
    public ResultView<List<LocationView>> getValidatorLocation() {
        List<LocationView> locationViewList = Lists.newArrayList();
        List<Validator> validatorList = validatorService.listValidator();
        for (Validator v : validatorList) {
            locationViewList.add(LocationView.builder()
                    .address(v.getAddress())
                    .city(v.getLocation().getCity())
                    .country(v.getLocation().getCountry())
                    .subdivision(v.getLocation().getSubdivision())
                    .latitude(v.getLocation().getLatitude())
                    .longitude(v.getLocation().getLongitude())
                    .build());
        }

        return ResultView.ok(locationViewList);
    }

    @ApiOperation(value = "Query my reward by page")
    @PostMapping(path = "/reward/detail", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<RewardDetailView>> listRewardDetail(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated AddressPageQuery addressPageQuery) {
        Pair<Long, List<Earning>> pair = Pair.of(0L, Lists.newArrayList());
        Optional<Validator> validatorOptional = validatorService.getByAddress(address);
        if (validatorOptional.isPresent()) {
            pair = earningService.listEarningByAddress(address, addressPageQuery.getPage().getIndex(), addressPageQuery.getPage().getLimit());
        } else {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        List<ObjectId> ids = pair.getRight().stream().map(Earning::getId).collect(Collectors.toList());
        Pair<List<String>, Map<ObjectId, List<EarningAsset>>> earningPair = earningService.mapEarningAssetByIds(ids);

        Map<String, Asset> assetMap = assetService.mapAssets(earningPair.getLeft());

        List<RewardDetailView> rewardDetailViewList = Lists.newArrayList();

        for (Earning earning : pair.getRight()) {
            List<EarningAssetView> earningAssetViewList = Lists.newArrayList();
            for (EarningAsset earningAsset : earningPair.getRight().get(earning.getId())) {
                Asset asset = assetMap.get(earningAsset.getAsset());

                earningAssetViewList.add(EarningAssetView.builder()
                        .asset(earningAsset.getAsset())
                        .name(asset.getName())
                        .symbol(asset.getSymbol())
                        .logo(asset.getLogo())
                        .value(String.valueOf(earningAsset.getValue()))
                        .build());
            }

            rewardDetailViewList.add(RewardDetailView.builder()
                    .height(earning.getHeight())
                    .time(earning.getTime())
                    .txHash(earning.getTxHash())
                    .earning(earningAssetViewList)
                    .build());
        }

        PageView pageView = PageView.of(pair.getLeft(), rewardDetailViewList);
        return ResultView.ok(pageView);

    }

    @ApiOperation(value = "Get balance via address")
    @PostMapping(path = "/balance")
    public ResultView<List<AssetView>> balance(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        List<AssetDTO> assetDTOList = balanceService.listBalance(address);
        List<AssetView> assetViewList = balanceConvertBehavior.convert(assetDTOList);
        if (CollectionUtils.isEmpty(assetViewList)) {
            AssetView assetView = assetConvertBehavior.convert(AssetSummary.builder().asset(ChainConstant.ASSET_ASIM).value(0L).build());
            assetViewList.add(assetView);
        }
        return ResultView.ok(assetViewList);
    }

    @ApiOperation(value = "List validator earning")
    @PostMapping(path = "/earning/list")
    public ResultView<List<ValidatorEarningView>> listEarning(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        List<ValidatorEarningView> validatorViewList = Lists.newArrayList();
        Optional<Validator> validatorOptional = validatorService.getByAddress(address);
        if (validatorOptional.isPresent()) {
            List<GroupSum> groupSumList = earningService.listEarningByAddress(address);
            List<String> assetIds = groupSumList.stream().map(GroupSum::get_id).collect(Collectors.toList());
            Map<String, Asset> assetMap = assetService.mapAssets(assetIds);
            List<EarningAssetView> earningAssetViewList = Lists.newArrayList();
            for (GroupSum groupSum : groupSumList) {
                Asset asset = assetMap.get(groupSum.get_id());
                earningAssetViewList.add(EarningAssetView.builder()
                        .asset(groupSum.get_id())
                        .name(asset.getName())
                        .symbol(asset.getSymbol())
                        .logo(asset.getLogo())
                        .value(String.valueOf(groupSum.getValue()))
                        .build());
            }

            validatorViewList.add(ValidatorEarningView.builder()
                    .address(address)
                    .earning(earningAssetViewList)
                    .build());
        } else {
            return ResultView.error(ErrorCode.PERMISSION_DENIED_ERROR);
        }

        return ResultView.ok(validatorViewList);
    }

    @ApiOperation(value = "Query total earning")
    @PostMapping("/earning/total")
    public ResultView<EarningView> getTotalEarning() {
        List<GroupSum> totalGroupSum = earningService.listTotalEarning();

        List<String> assetIds = totalGroupSum.stream().map(GroupSum::get_id).collect(Collectors.toList());
        Map<String, Asset> assetMap = assetService.mapAssets(assetIds);

        List<GroupSum> oneDayGroupSum = earningService.listOneDayEarning();

        List<EarningAssetView> totalEarning = Lists.newArrayList();
        List<EarningAssetView> oneDayEarning = Lists.newArrayList();

        for (GroupSum groupSum : totalGroupSum) {
            Asset asset = assetMap.get(groupSum.get_id());
            totalEarning.add(EarningAssetView.builder()
                    .asset(groupSum.get_id())
                    .name(asset.getName())
                    .symbol(asset.getSymbol())
                    .logo(asset.getLogo())
                    .value(String.valueOf(groupSum.getValue()))
                    .build());
        }

        for (GroupSum groupSum : oneDayGroupSum) {
            Asset asset = assetMap.get(groupSum.get_id());
            oneDayEarning.add(EarningAssetView.builder()
                    .asset(groupSum.get_id())
                    .name(asset.getName())
                    .symbol(asset.getSymbol())
                    .logo(asset.getLogo())
                    .value(String.valueOf(groupSum.getValue()))
                    .build());
        }

        return ResultView.ok(EarningView.builder()
                .totalEarning(totalEarning)
                .oneDayEarning(oneDayEarning)
                .build());
    }
}
