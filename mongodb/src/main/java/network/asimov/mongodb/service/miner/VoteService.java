package network.asimov.mongodb.service.miner;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.Vote;
import network.asimov.mongodb.pojo.VoteDTO;
import network.asimov.mongodb.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-27
 */
@Service("minerVoteService")
public class VoteService extends BaseService {
    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    /**
     * List vote detail information via transaction hash list
     *
     * @param txHash transaction hash list
     * @return vote detail information list
     */
    public List<VoteDTO> listVoteDTOByTxHash(List<String> txHash) {
        if (txHash == null) {
            return Lists.newArrayList();
        }
        List<VoteDTO> voteDTOList = Lists.newArrayList();
        Query query = new Query(Criteria.where("tx_hash").in(txHash));
        List<Vote> voteList = mongoTemplate.find(query, Vote.class);
        List<Proposal> proposalList = proposalService.listProposalByIds(voteList.stream().map(Vote::getProposalId).distinct().collect(Collectors.toList()));
        Map<Long, Proposal> proposalMap = proposalList.stream().collect(Collectors.toMap(Proposal::getProposalId, proposal -> proposal));
        for (Vote vote : voteList) {
            VoteDTO voteDTO = VoteDTO.builder().build();
            BeanUtils.copyProperties(vote, voteDTO);
            Proposal proposal = proposalMap.get(vote.getProposalId());
            if (proposal != null) {
                voteDTO.setStatus(proposal.getStatus());
                voteDTO.setTime(proposal.getTime());
                voteDTO.setType(proposal.getType());
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
