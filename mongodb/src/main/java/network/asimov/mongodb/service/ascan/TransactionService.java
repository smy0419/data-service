package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.Transaction;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
@Service("transactionService")
public class TransactionService extends BaseService {

    /**
     * Get transactions via block height
     *
     * @param height block height
     * @return transaction list
     */
    public Pair<Long, List<Transaction>> listTransaction(long height, Integer index, Integer limit) {
        Query query = new Query(Criteria.where("height").is(height));
        query.fields().include("hash");
        query.fields().include("time");
        query.fields().include("fee");
        return queryByPage(index, limit, query, Transaction.class,null);
    }

    /**
     * Get transaction information via transaction hash
     *
     * @param hash transaction hash
     * @return transaction information
     */
    public Optional<Transaction> get(String hash) {
        Query query = new Query(Criteria.where("hash").is(hash));
        Transaction transaction = mongoTemplate.findOne(query, Transaction.class);
        return Optional.ofNullable(transaction);
    }

    public Optional<Transaction> get(long height, String hash) {
        Query query = new Query(Criteria.where("height").is(height).and("hash").is(hash));
        Transaction transaction = mongoTemplate.findOne(query, Transaction.class);
        return Optional.ofNullable(transaction);
    }

    /**
     * Count transactions
     *
     * @return transaction count
     */
    public long count() {
        return mongoTemplate.count(new Query(), Transaction.class);
    }
}
