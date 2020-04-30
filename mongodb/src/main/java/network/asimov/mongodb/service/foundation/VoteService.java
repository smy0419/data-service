package network.asimov.mongodb.service.foundation;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.GroupCount;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.Vote;
import network.asimov.mongodb.pojo.VoteDTO;
import network.asimov.mongodb.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Service("foundationVoteService")
public class VoteService extends BaseService {
    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    public Map<String, Long> getVoteCountMap() {
        Map<String, Long> voteCountMap = new HashMap<>(24);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("voter").count().as("count")
        );
        List<GroupCount> result = findAggregateList(aggregation, "foundation_vote", GroupCount.class);
        for (GroupCount groupCount : result) {
            voteCountMap.put(groupCount.get_id(), groupCount.getCount());
        }
        return voteCountMap;
    }

    public List<VoteDTO> listVoteDTOByTxHash(List<String> txHash) {
        if (txHash == null) {
            return Lists.newArrayList();
        }
        List<VoteDTO> voteDTOList = Lists.newArrayList();
        Query query = new Query(Criteria.where("tx_hash").in(txHash));
        List<Vote> voteList = mongoTemplate.find(query, Vote.class);
        // 根据proposal id 查询提议详情
        List<Proposal> proposalList = proposalService.listProposalByIds(voteList.stream().map(Vote::getProposalId).distinct().collect(Collectors.toList()));
        // 转成map
        Map<Long, Proposal> proposalMap = proposalList.stream().collect(Collectors.toMap(Proposal::getProposalId, proposal -> proposal));
        for (Vote vote : voteList) {
            VoteDTO voteDTO = VoteDTO.builder().build();
            BeanUtils.copyProperties(vote, voteDTO);
            Proposal proposal = proposalMap.get(vote.getProposalId());
            if (proposal != null) {
                voteDTO.setStatus(proposal.getStatus());
                voteDTO.setTime(proposal.getTime());
                voteDTO.setType(proposal.getProposalType());
                voteDTO.setProposalTxHash(proposal.getTxHash());
                voteDTO.setAddress(proposal.getAddress());
            } else {
                throw BusinessException.builder().message(String.format("proposal not exist, proposal_id: %d", vote.getProposalId())).errorCode(ErrorCode.DATA_ERROR).build();
            }
            voteDTOList.add(voteDTO);
        }

        return voteDTOList;
    }

    public List<Vote> listVoteByProposalId(long proposalId) {
        Query query = new Query(
                Criteria.where("proposal_id").is(proposalId)
        );
        return mongoTemplate.find(query, Vote.class);
    }
}
