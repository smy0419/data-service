package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.Block;
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
 * @date 2019-09-26
 */
@Service("blockService")
public class BlockService extends BaseService {

    /**
     * Get current synchronized block height
     *
     * @return block height
     */
    public Optional<Long> getHandledBlockHeight() {
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "height"));
        Block block = mongoTemplate.findOne(query, Block.class);
        return block != null ? Optional.of(block.getHeight()) : Optional.empty();
    }

    /**
     * Paging to get blocks
     *
     * @param index current page
     * @param limit page limit
     * @return <total count, block list>
     */
    public Pair<Long, List<Block>> queryBlockByPage(Integer index, Integer limit) {
        Query query = new Query();
        return queryByPageTopN(1000, index, limit, query, Block.class,  Sort.Direction.DESC, "height");
    }

    /**
     * Get block information via block height
     *
     * @param height block height
     * @return block information
     */
    public Optional<Block> getBlock(long height) {
        Query query = new Query(Criteria.where("height").is(height));
        Block block = mongoTemplate.findOne(query, Block.class);
        return block != null ? Optional.of(block) : Optional.empty();
    }

    /**
     * Get block information via block hash
     *
     * @param hash block hash
     * @return block information
     */
    public Optional<Block> getBlock(String hash) {
        Query query = new Query(Criteria.where("hash").is(hash));
        Block block = mongoTemplate.findOne(query, Block.class);
        return block != null ? Optional.of(block) : Optional.empty();
    }
}
