package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.GroupSum;
import network.asimov.mongodb.entity.ascan.Trading;
import network.asimov.mongodb.service.BaseService;
import network.asimov.util.AssetUtil;
import network.asimov.util.TimeUtil;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-26
 */
@Service("tradingService")
public class TradingService extends BaseService {
    /**
     * Get 24-hour asset's trading volume
     *
     * @param asset asset string
     * @return trading volume
     */
    public long tradingVolume(String asset) {
        long from = TimeUtil.currentSeconds() - TimeUtil.SECONDS_OF_DAY;
        Criteria criteria = Criteria.where("time").gt(from).and("asset").is(asset);

        if (AssetUtil.indivisible(asset)) {
            // indivisible asset
            Query query = new Query(criteria);
            return mongoTemplate.count(query, Trading.class);
        } else {
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group("asset").sum("value").as("value")
            );

            List<GroupSum> result = findAggregateList(aggregation, "trading", GroupSum.class);
            if (!result.isEmpty()) {
                return result.get(0).getValue();
            }
        }
        return 0L;
    }
}
