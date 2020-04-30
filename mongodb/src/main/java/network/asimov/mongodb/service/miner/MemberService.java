package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Member;
import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-09-27
 */
@Service("minerMemberService")
public class MemberService extends BaseService {
    @Resource(name = "minerRoundService")
    private RoundService roundService;

    /**
     * Get mining member of current round
     *
     * @param address address
     * @return mining member information
     */
    public Optional<Member> getCurrentMiningMember(String address) {
        Round round = roundService.getCurrentRound();
        Query query = new Query(Criteria.where("round").is(round.getRound()).and("address").is(address));
        Member member = mongoTemplate.findOne(query, Member.class);
        return Optional.ofNullable(member);
    }

    /**
     * Determines whether the given address is mining member of the current round
     *
     * @param address address
     * @return is or not
     */
    public boolean exist(String address) {
        Optional<Member> member = this.getCurrentMiningMember(address);
        return member.isPresent();
    }

    public Pair<Long, List<Member>> listCurrentMiningMember(Integer index, Integer limit) {
        Round round = roundService.getCurrentRound();
        Query query = new Query(Criteria.where("round").is(round.getRound()));
        return queryByPage(index, limit, query, Member.class, Sort.Direction.DESC, "produced");
    }

    public List<Member> listMemberByRound(long round) {
        Query query = new Query(
                Criteria.where("round").is(round)
        );
        return mongoTemplate.find(query, Member.class);
    }
}
