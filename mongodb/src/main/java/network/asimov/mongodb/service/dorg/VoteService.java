package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.Vote;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Service("daoVoteService")
public class VoteService extends BaseService {
    public List<Vote> listVoteByProposalId(long proposalId, String contractAddress) {
        Query query = new Query(
                Criteria.where("vote_id").is(proposalId).and("contract_address").is(contractAddress)
        );
        return mongoTemplate.find(query, Vote.class);
    }
}
