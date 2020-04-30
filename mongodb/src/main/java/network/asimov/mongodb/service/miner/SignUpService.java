package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.entity.miner.SignUp;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-09-29
 */
@Service("minerSignUpService")
public class SignUpService extends BaseService {
    @Resource(name = "minerRoundService")
    private RoundService roundService;

    /**
     * Paging to get applicants of next round, order by produced blocks desc
     *
     * @param index current page
     * @param limit page limit
     * @return <total count, applicants list>
     */
    public Pair<Long, List<SignUp>> listNextRoundSignUp(Integer index, Integer limit) {
        Round round = roundService.getCurrentRound();
        Query query = new Query(Criteria.where("round").is(round.getRound() + 1));
        return queryByPage(index, limit, query, SignUp.class, Sort.Direction.DESC, "produced", "efficiency");
    }
}
