package network.asimov.mongodb.service.ascan;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhangjing
 * @date 2019-09-20
 */
@Service("assetService")
public class AssetService extends BaseService {

    /**
     * Get asset information
     *
     * @param assetId asset string
     * @return asset information
     */
    public Optional<Asset> getAsset(String assetId) {
        Query query = new Query(Criteria.where("asset").is(assetId));
        Asset asset = mongoTemplate.findOne(query, Asset.class);
        return Optional.ofNullable(asset);
    }

    /**
     * List assets
     *
     * @param assetIds asset string list
     * @return asset list
     */
    public List<Asset> listAsset(List<String> assetIds) {
        if (assetIds == null) {
            return Lists.newArrayList();
        }
        Query query = new Query(Criteria.where("asset").in(assetIds));
        return mongoTemplate.find(query, Asset.class);
    }

    /**
     * Map Assets
     *
     * @param assetIds asset string list
     * @return asset map
     */
    public Map<String, Asset> mapAssets(List<String> assetIds) {
        List<Asset> list = listAsset(assetIds);
        return list.stream().collect(Collectors.toMap(Asset::getAsset, asset -> asset));
    }

    /**
     * List all assets
     *
     * @return asset list
     */
    public List<Asset> listAllAsset() {
        Query query = new Query();
        return mongoTemplate.find(query, Asset.class);
    }

    /**
     * Paging to get assets
     *
     * @param index     current page
     * @param limit     page limit
     * @param assetName asset name
     * @return <total count, asst list>
     */
    public Pair<Long, List<Asset>> listAssetByPageAndName(Integer index, Integer limit, String assetName) {
        Query query = new Query();
        if (StringUtils.isNotBlank(assetName)) {
            query.addCriteria(Criteria.where("name").is(assetName));
        }
        return queryByPage(index, limit, query, Asset.class, Sort.Direction.DESC, "height");
    }
}
