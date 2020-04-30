package network.asimov.controller.miner;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.pojo.VoteDTO;
import network.asimov.mongodb.service.miner.RoundService;
import network.asimov.mongodb.service.miner.VoteService;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.IdQuery;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.common.VoteRequest;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.TxView;
import network.asimov.response.miner.VoteView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-27
 */
@CrossOrigin
@RestController("minerVoteController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON, consumes = RequestConstants.CONTENT_TYPE_JSON)
public class VoteController {

    @Resource(name = "minerVoteService")
    private VoteService voteService;

    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "minerRoundService")
    private RoundService roundService;

    @Resource(name = "minerSendDeletableTransaction")
    private SendTransactionBehavior minerSendDeletableTransaction;

    @Resource(name = "minerVoteCheck")
    private CheckBehavior minerVoteCheck;

    @ApiOperation(value = "Query my voted proposal by page")
    @PostMapping(path = "/vote/mine", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<VoteView>> listMyVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated PurePageQuery purePageQuery) {
        List<VoteView> voteViewList = Lists.newArrayList();

        Pair<Long, List<TMinerOperation>> pair = minerOperationService.listTMinerOperationByAddressAndType(address, MinerOperationType.Vote.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<VoteDTO> voteDTOList = voteService.listVoteDTOByTxHash(pair.getRight().stream().map(TMinerOperation::getTxHash).distinct().collect(Collectors.toList()));
        Map<String, VoteDTO> voteMap = voteDTOList.stream().collect(Collectors.toMap(VoteDTO::getTxHash, voteDTO -> voteDTO));
        List<String> addressList = voteDTOList.stream().map(VoteDTO::getAddress).distinct().collect(Collectors.toList());

        List<Account> list = daoAccountService.listAll(addressList);
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<String> txHashList = voteDTOList.stream().map(VoteDTO::getProposalTxHash).distinct().collect(Collectors.toList());
        Map<String, TMinerOperation> tMinerOperationMap = minerOperationService.mapTMinerOperationByTxHash(txHashList);

        for (TMinerOperation tMinerOperation : pair.getRight()) {
            VoteDTO voteDTO = voteMap.get(tMinerOperation.getTxHash());

            VoteView voteView = VoteView.builder()
                    .txStatus(tMinerOperation.getTxStatus())
                    .decision(JSON.parseObject(tMinerOperation.getAdditionalInfo()).getBoolean(OperationAdditionalKey.DECISION))
                    .build();
            if (voteDTO != null) {
                TMinerOperation proposalOperation = tMinerOperationMap.get(voteDTO.getProposalTxHash());
                Account account = userMap.get(voteDTO.getAddress());
                voteView.setProposalId(voteDTO.getProposalId());
                voteView.setType(voteDTO.getType());
                voteView.setTime(voteDTO.getTime());
                if (proposalOperation != null) {
                    voteView.setComment(JSON.parseObject(proposalOperation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT));
                } else {
                    voteView.setComment("");
                }
                voteView.setProposalStatus(voteDTO.getStatus());
                voteView.setProposer(PersonView.builder()
                        .name(account != null ? account.getName() : "")
                        .build());
            } else {
                voteView.setProposalId(-1);
                voteView.setType(-1);
                voteView.setTime(-1);
                voteView.setComment("");
                voteView.setProposalStatus(-1);
                voteView.setProposer(PersonView.builder().name("").build());
            }
            voteViewList.add(voteView);
        }

        PageView<VoteView> pageView = PageView.of(pair.getLeft(), voteViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Check vote")
    @PostMapping(path = "/committee/vote/check", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView checkVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                @RequestBody @Validated IdQuery idQuery) {
        Map<String, Object> args = CheckArgsUtil.generate(address, idQuery.getId());
        return minerVoteCheck.check(args);
    }

    @ApiOperation(value = "Vote", notes = "call /miner/contract returns abi => vote(uint proposalId, bool decision)")
    @PostMapping(path = "/committee/vote", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<TxView> vote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                   @RequestBody @Validated VoteRequest voteRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generate(address, voteRequest.getProposalId());
        ResultView check = minerVoteCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Round round = roundService.getCurrentRound();
        Map<String, Object> additionalInfo = new HashMap<>(2);
        additionalInfo.put(OperationAdditionalKey.PROPOSAL_ID, voteRequest.getProposalId());
        additionalInfo.put(OperationAdditionalKey.DECISION, voteRequest.getDecision());
        Map<String, Object> args = SendTxArgsUtil.generate(round.getRound(), MinerOperationType.Vote.getCode(), additionalInfo, address);
        return minerSendDeletableTransaction.perform(voteRequest.getCallData(), args);
    }
}
