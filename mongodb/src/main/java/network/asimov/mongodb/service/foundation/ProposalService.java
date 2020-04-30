package network.asimov.mongodb.service.foundation;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.GroupCount;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Service("foundationProposalService")
public class ProposalService extends BaseService {
    /**
     * Get proposal count via address
     *
     * @return proposal count map
     */
    public Map<String, Long> getProposalCountMap() {
        Map<String, Long> proposalCountMap = new HashMap<>(24);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("address").count().as("count")
        );
        List<GroupCount> result = findAggregateList(aggregation, "foundation_proposal", GroupCount.class);
        for (GroupCount groupCount : result) {
            proposalCountMap.put(groupCount.get_id(), groupCount.getCount());
        }
        return proposalCountMap;
    }

    /**
     * Get proposal detail information via proposal id
     *
     * @param proposalId proposal id
     * @return proposal detail information
     */
    public Optional<Proposal> getProposalById(long proposalId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("proposal_id").is(proposalId));
        Proposal proposal = mongoTemplate.findOne(query, Proposal.class);
        return Optional.ofNullable(proposal);
    }

    /**
     * Get proposal detail information via transaction hash
     *
     * @param txHash transaction hash
     * @return
     */
    public Optional<Proposal> getProposalByHash(String txHash) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tx_hash").is(txHash));
        Proposal proposal = mongoTemplate.findOne(query, Proposal.class);
        return Optional.ofNullable(proposal);
    }

    /**
     * List proposal detail information via proposal id list
     *
     * @param proposalIds proposal id list
     * @return proposal detail information list
     */
    public List<Proposal> listProposalByIds(List<Long> proposalIds) {
        if (proposalIds == null) {
            return Lists.newArrayList();
        }
        Query query = new Query(Criteria.where("proposal_id").in(proposalIds));
        return mongoTemplate.find(query, Proposal.class);
    }

    /**
     * List proposal detail information via transaction hash list
     *
     * @param txHash transaction hash list
     * @return proposal detail information list
     */
    public List<Proposal> listProposalByTxHash(List<String> txHash) {
        if (txHash == null) {
            return Lists.newArrayList();
        }
        Query query = new Query(Criteria.where("tx_hash").in(txHash));
        return mongoTemplate.find(query, Proposal.class);
    }

    /**
     * Map proposal detail information via transaction hash list
     *
     * @param txHash transaction hash list
     * @return proposal detail information map
     */
    public Map<String, Proposal> mapProposalByTxHash(List<String> txHash) {
        List<Proposal> list = listProposalByTxHash(txHash);
        return list.stream().collect(Collectors.toMap(Proposal::getTxHash, proposal -> proposal));
    }
}
