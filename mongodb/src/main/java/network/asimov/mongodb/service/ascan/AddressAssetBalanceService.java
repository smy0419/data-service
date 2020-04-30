package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.AddressAssetBalance;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author sunmengyuan
 * @date 2019-11-07
 */
@Service("addressAssetBalanceService")
public class AddressAssetBalanceService extends BaseService {

    /**
     * Count holders via asset
     *
     * @param asset asset string
     * @return count of holder
     */
    public Long countHoldersByAsset(String asset) {
        Query query = new Query(Criteria.where("asset").is(asset).and("balance").gt(0));
        return mongoTemplate.count(query, AddressAssetBalance.class);
    }
}

