package network.asimov.mongodb.service.validator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.asimov.mongodb.entity.GroupSum;
import network.asimov.mongodb.entity.validator.Earning;
import network.asimov.mongodb.entity.validator.EarningAsset;
import network.asimov.mongodb.service.BaseService;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Service("validatorEarningService")
public class EarningService extends BaseService {
    public List<GroupSum> listTotalEarning() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("asset").sum("value").as("value")
        );

        List<GroupSum> result = findAggregateList(aggregation, "earning_asset", GroupSum.class);
        return result;
    }

    public List<GroupSum> listOneDayEarning() {
        long from = TimeUtil.currentSeconds() - TimeUtil.SECONDS_OF_DAY;
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("time").gt(from)),
                Aggregation.group("asset").sum("value").as("value")
        );

        List<GroupSum> result = findAggregateList(aggregation, "earning_asset", GroupSum.class);
        return result;
    }

    public List<GroupSum> listEarningByAddress(String address) {
        Query query = new Query(Criteria.where("address").is(address));
        List<Earning> earnings = mongoTemplate.find(query, Earning.class);
        List<ObjectId> ids = earnings.stream().map(Earning::getId).collect(Collectors.toList());

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("earning_id").in(ids)),
                Aggregation.group("asset").sum("value").as("value")
        );

        List<GroupSum> result = findAggregateList(aggregation, "earning_asset", GroupSum.class);
        return result;
    }

    public Pair<Long, List<Earning>> listEarningByAddress(String address, Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(address));
        return queryByPage(index, limit, query, Earning.class, Sort.Direction.DESC, "time");
    }

    public Pair<List<String>, Map<ObjectId, List<EarningAsset>>> mapEarningAssetByIds(List<ObjectId> ids) {
        if (ids == null) {
            return Pair.of(Lists.newArrayList(), Maps.newHashMap());
        }
        List<String> assetIds = Lists.newArrayList();
        Map<ObjectId, List<EarningAsset>> map = Maps.newHashMap();
        Query query = new Query(Criteria.where("earning_id").in(ids));
        List<EarningAsset> earningAssetList = mongoTemplate.find(query, EarningAsset.class);
        for (EarningAsset earningAsset : earningAssetList) {
            if (map.containsKey(earningAsset.getEarningId())) {
                map.get(earningAsset.getEarningId()).add(earningAsset);
            } else {
                map.put(earningAsset.getEarningId(), Lists.newArrayList(earningAsset));
            }
            if (!assetIds.contains(earningAsset.getAsset())) {
                assetIds.add(earningAsset.getAsset());
            }
        }
        return Pair.of(assetIds, map);
    }

    public Long countEarningByAddress(String address) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(address));
        return mongoTemplate.count(query, Earning.class);
    }
}
