package network.asimov.scheduler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.service.dorg.ProposalService;
import network.asimov.mysql.constant.DaoMessage;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TDaoMessage;
import network.asimov.mysql.service.dorg.DaoMessageService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2020-02-05
 */

@Component
@Slf4j
public class ProposalStatusScheduler {
    @Resource(name = "daoProposalService")
    private ProposalService proposalService;

    @Resource(name = "daoMessageService")
    private DaoMessageService daoMessageService;

    @Scheduled(fixedRate = 3600 * 1000)
    public void updateProposalStatus() {
        List<Proposal> proposalList = proposalService.updateExpiredProposal();

        for (Proposal proposal : proposalList) {
            Map<String, Object> additionalInfoMap = Maps.newHashMap();
            additionalInfoMap.put(OperationAdditionalKey.PROPOSAL_ID, proposal.getProposalId());

            TDaoMessage message = new TDaoMessage();
            message.setCategory(DaoMessage.Category.ProposalExpired.getCode());
            message.setType(DaoMessage.Type.ReadOnly.ordinal());
            message.setContractAddress(proposal.getContractAddress());
            message.setMessagePosition(DaoMessage.Position.Both.ordinal());
            message.setReceiver(Strings.EMPTY);
            message.setAdditionalInfo(JSON.toJSONString(additionalInfoMap));
            message.setState(DaoMessage.State.Unread.ordinal());

            daoMessageService.saveMessage(message);
        }
    }
}
