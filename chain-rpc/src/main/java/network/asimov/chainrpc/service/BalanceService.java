package network.asimov.chainrpc.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.chainrpc.request.ChainRequest;
import network.asimov.mongodb.entity.ascan.AddressAssetBalance;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.service.ascan.AddressAssetBalanceService;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.util.AssetUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static network.asimov.chainrpc.constant.ChainConstant.ASSET_ASIM;

/**
 * @author zhangjing
 * @date 2019-09-20
 */

@Service("balanceService")
public class BalanceService {
    private final static String RPC_GET_BALANCE = "getBalance";

    @Resource(name = "chainRpcService")
    private ChainRpcService chainRpcService;

    @Resource(name = "addressAssetBalanceService")
    private AddressAssetBalanceService addressAssetBalanceService;

    @Resource(name = "assetService")
    private AssetService assetService;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    /**
     * Get balance list by address
     *
     * @param address address
     */
    public List<AssetDTO> listBalanceFromDatabase(String address) {
        // Get balance from address_asset_balance collection in mongodb
        List<AddressAssetBalance> balanceList = addressAssetBalanceService.listBalanceByAddress(address);
        // Get additional information about the asset.
        List<String> assetIdsList = balanceList.stream().map(AddressAssetBalance::getAsset).collect(Collectors.toList());
        Map<String, Asset> assetMap = assetService.mapAssets(assetIdsList);

        // Packaged into AssetDTO.
        Map<String, AssetDTO> assetDTOMap = Maps.newHashMap();
        balanceList.forEach(v -> {
            String assetId = v.getAsset();
            String value = String.valueOf(v.getBalance());

            // If it is an indivisible asset, query it's child description.
            String indivisibleDesc = StringUtils.EMPTY;
            if (AssetUtil.indivisible(assetId)) {
                indivisibleDesc = daoIndivisibleAssetService.getIndivisibleDesc(assetId, Long.valueOf(value));
            }

            /*
             * Because the data returned by chain only have one dada for a divisible asset,
             * as long as it appears for the second time, it must be an indivisible asset
             */
            if (!assetDTOMap.containsKey(assetId)) {
                AssetDTO assetDTO = AssetDTO.builder().asset(assetId).value(value).assetMap(assetMap).indivisibleDesc(indivisibleDesc).build();
                assetDTOMap.put(assetId, assetDTO);
            } else {
                /*
                 * The asset that exists in assetDTOMap must be an indivisible asset,
                 * and don't need to judge it through AssetUtil.indivisible.
                 */
                AssetDTO assetDTO = assetDTOMap.get(assetId);
                assetDTO.addIndivisible(value, indivisibleDesc);
            }
        });
        List<AssetDTO> assetDTOList = new ArrayList<>(assetDTOMap.values());
        Collections.sort(assetDTOList);
        for (AssetDTO assetDTO : assetDTOList) {
            if (CollectionUtils.isNotEmpty(assetDTO.getIndivisibleList())) {
                Collections.sort(assetDTO.getIndivisibleList());
            }
        }
        return assetDTOList;
    }


    public List<AssetDTO> listBalance(String address) {
        // Get balance from chain
        List<Object> params = Lists.newArrayList(address);
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_GET_BALANCE).params(params).build();
        JSONArray jsonArray = (JSONArray) chainRpcService.post(chainRequest);
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Lists.newArrayList();
        }

        // Get additional information about the asset.
        Set<String> assetIdsSet = jsonArray.stream().map(v -> ((JSONObject) v).getString("asset")).collect(Collectors.toSet());
        List<String> assetIdsList = new ArrayList<>(assetIdsSet);
        Map<String, Asset> assetMap = assetService.mapAssets(assetIdsList);

        // Packaged into AssetDTO.
        Map<String, AssetDTO> assetDTOMap = Maps.newHashMap();
        jsonArray.forEach(v -> {
            String assetId = ((JSONObject) v).getString("asset");
            String value = ((JSONObject) v).getString("value");

            // If it is an indivisible asset, query it's child description.
            String indivisibleDesc = StringUtils.EMPTY;
            if (AssetUtil.indivisible(assetId)) {
                indivisibleDesc = daoIndivisibleAssetService.getIndivisibleDesc(assetId, Long.valueOf(value));
            }

            /*
             * Because the data returned by chain only have one dada for a divisible asset,
             * as long as it appears for the second time, it must be an indivisible asset
             */
            if (!assetDTOMap.containsKey(assetId)) {
                AssetDTO assetDTO = AssetDTO.builder().asset(assetId).value(value).assetMap(assetMap).indivisibleDesc(indivisibleDesc).build();
                assetDTOMap.put(assetId, assetDTO);
            } else {
                /*
                 * The asset that exists in assetDTOMap must be an indivisible asset,
                 * and don't need to judge it through AssetUtil.indivisible.
                 */
                AssetDTO assetDTO = assetDTOMap.get(assetId);
                assetDTO.addIndivisible(value, indivisibleDesc);
            }
        });
        List<AssetDTO> assetDTOList = new ArrayList<>(assetDTOMap.values());
        Collections.sort(assetDTOList);
        for (AssetDTO assetDTO : assetDTOList) {
            if (CollectionUtils.isNotEmpty(assetDTO.getIndivisibleList())) {
                Collections.sort(assetDTO.getIndivisibleList());
            }
        }
        return assetDTOList;
    }

    /**
     * GET ASIMOV
     *
     * @param address address
     * @return AssetDTO
     */
    public Optional<AssetDTO> getAsimov(String address) {
        List<Object> params = Lists.newArrayList(address);
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_GET_BALANCE).params(params).build();
        JSONArray jsonArray = (JSONArray) chainRpcService.post(chainRequest);
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Optional.empty();
        }

        Map<String, Asset> assetMap = assetService.mapAssets(Lists.newArrayList(ASSET_ASIM));
        for (Object v : jsonArray) {
            String assetId = ((JSONObject) v).getString("asset");
            String value = ((JSONObject) v).getString("value");
            if (assetId.equals(ASSET_ASIM)) {
                AssetDTO assetDTO = AssetDTO.builder().asset(assetId).value(value).assetMap(assetMap).build();
                return Optional.of(assetDTO);
            }
        }

        return Optional.empty();
    }
}