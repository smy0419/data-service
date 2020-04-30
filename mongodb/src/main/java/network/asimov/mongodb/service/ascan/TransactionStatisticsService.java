package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.TransactionCount;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2020/4/23
 */
@Service("transactionStatisticsService")
public class TransactionStatisticsService extends BaseService {

    public Optional<TransactionCount> get(String key) {
        Query query = new Query(Criteria.where("key").is(key));
        TransactionCount transactionCount = mongoTemplate.findOne(query, TransactionCount.class);
        return Optional.ofNullable(transactionCount);
    }

    public long count(String key) {
        Optional<TransactionCount> transactionCount = this.get(key);
        return transactionCount.map(TransactionCount::getTxCount).orElse(0L);
    }

    public Pair<Long, List<TransactionCount>> queryDataByPage(TransactionCount.TxCountCategory category, Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("category").is(category.getCode()));
        return queryByPageTopN(1000, index, limit, query, TransactionCount.class, Sort.Direction.DESC, "_id");
    }

    public <T> Pair<Long, List<T>> queryTxListByPage(Class<T> clazz, String key, Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("key").is(key));
        return queryByPageTopN(1000, index, limit, query, clazz, Sort.Direction.DESC, "_id");
    }
}
