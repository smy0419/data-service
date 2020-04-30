package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author zhangjing
 * @date 2019-09-27
 */
@Service("minerRoundService")
public class RoundService extends BaseService {

    /**
     * 获取当前轮次信息Get current round information
     *
     * @return round information
     */
    public Round getCurrentRound() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "round"));
        Round round = mongoTemplate.findOne(query, Round.class);
        return round;
    }
}
