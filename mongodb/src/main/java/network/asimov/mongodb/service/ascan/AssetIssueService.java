package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.AssetIssue;
import network.asimov.mongodb.service.BaseService;
import network.asimov.util.AssetUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-20
 */

@Service("assetIssueService")
public class AssetIssueService extends BaseService {

    /**
     * List asset issue records
     *
     * @param assetId asset string
     * @return asset issue list
     */
    public List<AssetIssue> listAssetIssue(String assetId) {
        Query query = new Query(Criteria.where("asset").is(assetId));
        return mongoTemplate.find(query, AssetIssue.class);
    }

    /**
     * Sum asset issue amount
     *
     * @param assetId asset string
     * @return issue amount
     */
    public long sumAssetIssue(String assetId) {
        long amount;
        List<AssetIssue> assetIssueList = this.listAssetIssue(assetId);
        if (AssetUtil.indivisible(assetId)) {
            // indivisible asset
            amount = assetIssueList.size();
        } else {
            // divisible asset
            amount = assetIssueList.stream().mapToLong(AssetIssue::getValue).sum();
        }

        return amount;
    }

    /**
     * Paging to get asset issue records
     *
     * @param index current page
     * @param limit page limit
     * @param asset asset string
     * @return <total count, asset issue list>
     */
    public Pair<Long, List<AssetIssue>> queryAssetIssue(Integer index, Integer limit, String asset) {
        Query query = new Query();
        query.addCriteria(Criteria.where("asset").is(asset));
        return queryByPage(index, limit, query, AssetIssue.class, Sort.Direction.ASC, "height");
    }
}
