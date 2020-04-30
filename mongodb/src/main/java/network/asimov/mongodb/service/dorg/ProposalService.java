package network.asimov.mongodb.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.service.BaseService;
import network.asimov.util.TimeUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-06
 */
@Service("daoProposalService")
public class ProposalService extends BaseService {
    public List<Proposal> listProposalByIds(String contractAddress, List<Long> proposalIds) {
        if (proposalIds == null) {
            return Lists.newArrayList();
        }
        Query query = new Query(Criteria.where("contract_address").is(contractAddress).and("proposal_id").in(proposalIds));
        return mongoTemplate.find(query, Proposal.class);
    }

    public Optional<Proposal> getProposalById(long proposalId, String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("proposal_id").is(proposalId).and("contract_address").is(contractAddress));
        Proposal proposal = mongoTemplate.findOne(query, Proposal.class);
        return Optional.ofNullable(proposal);
    }

    public Optional<Proposal> getProposalByHash(String txHash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tx_hash").is(txHash));
        Proposal proposal = mongoTemplate.findOne(query, Proposal.class);
        return Optional.ofNullable(proposal);
    }

    public List<Proposal> updateExpiredProposal() {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(Proposal.Status.OnGoing.ordinal()).and("end_time").lte(TimeUtil.currentSeconds()));
        List<Proposal> proposalList = mongoTemplate.find(query, Proposal.class);

        Update update = new Update();
        update.set("status", Proposal.Status.Expired.ordinal());
        mongoTemplate.updateMulti(query, update, Proposal.class);
        return proposalList;
    }
}
