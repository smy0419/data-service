package network.asimov.controller.miner;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.constant.Condition;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.miner.*;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.miner.*;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.miner.MinerOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.IdQuery;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.miner.ProposalCheckRequest;
import network.asimov.request.miner.ProposalRequest;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.TxView;
import network.asimov.response.common.VoterView;
import network.asimov.response.miner.ProposalDetailView;
import network.asimov.response.miner.ProposalView;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-26
 */
@Slf4j
@CrossOrigin
@RestController("minerProposalController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ProposalController {
    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "minerVoteService")
    private VoteService voteService;

    @Resource(name = "minerTodoService")
    private TodoService todoService;

    @Resource(name = "minerMemberService")
    private MemberService memberService;

    @Resource(name = "minerRoundService")
    private RoundService roundService;

    @Resource(name = "minerSendDeletableTransaction")
    private SendTransactionBehavior minerSendDeletableTransaction;

    @Resource(name = "minerVoteCheck")
    private CheckBehavior minerVoteCheck;

    @Resource(name = "minerLaunchCheck")
    private CheckBehavior minerLaunchCheck;

    @Resource
    private AssetService assetService;

    @ApiOperation(value = "Query proposal history by page")
    @PostMapping(path = "/proposal/history", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ProposalView>> listHistoryProposal(@RequestBody @Validated PurePageQuery purePageQuery) {
        List<ProposalView> proposalViewList = Lists.newArrayList();
        Pair<Long, List<TMinerOperation>> pair = minerOperationService.listTMinerOperationByType(MinerOperationType.Proposal.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        Map<String, Proposal> proposalMap = proposalService.mapProposalByTxHash(pair.getRight().stream().map(TMinerOperation::getTxHash).collect(Collectors.toList()));
        List<Account> list = daoAccountService.listAll(pair.getRight().stream().map(TMinerOperation::getOperator).distinct().collect(Collectors.toList()));
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        long nowSecond = System.currentTimeMillis() / 1000;
        for (TMinerOperation tMinerOperation : pair.getRight()) {
            Proposal proposal = proposalMap.get(tMinerOperation.getTxHash());

            if (proposal != null && proposal.getEndTime() < nowSecond && proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                proposal.setStatus(Proposal.Status.Rejected.ordinal());
            }
            Account account = userMap.get(tMinerOperation.getOperator());
            proposalViewList.add(ProposalView.builder()
                    .comment(JSON.parseObject(tMinerOperation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT))
                    .proposer(PersonView.builder()
                            .name(account != null ? account.getName() : "")
                            .build())
                    .time(tMinerOperation.getCreateTime())
                    .proposalStatus(proposal != null ? proposal.getStatus() : -1)
                    .txStatus(tMinerOperation.getTxStatus())
                    .proposalId(proposal != null ? proposal.getProposalId() : -1)
                    .build());
        }
        PageView<ProposalView> pageView = PageView.of(pair.getLeft(), proposalViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Query my proposal by page")
    @PostMapping(path = "/proposal/mine", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ProposalView>> listMyProposal(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated PurePageQuery purePageQuery) {
        List<ProposalView> proposalViewList = Lists.newArrayList();
        Pair<Long, List<TMinerOperation>> pair = minerOperationService.listTMinerOperationByAddressAndType(address, MinerOperationType.Proposal.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());

        Map<String, Proposal> proposalMap = proposalService.mapProposalByTxHash(pair.getRight().stream().map(TMinerOperation::getTxHash).distinct().collect(Collectors.toList()));

        long nowSecond = TimeUtil.currentSeconds();
        for (TMinerOperation tMinerOperation : pair.getRight()) {
            ProposalView proposalView = ProposalView.builder()
                    .comment(JSON.parseObject(tMinerOperation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT))
                    .txStatus(tMinerOperation.getTxStatus())
                    .build();
            Proposal proposal = proposalMap.get(tMinerOperation.getTxHash());
            if (proposal != null && proposal.getEndTime() < nowSecond && proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                proposal.setStatus(Proposal.Status.Rejected.ordinal());
            }

            if (proposal != null) {
                proposalView.setProposalId(proposal.getProposalId());
                proposalView.setProposalStatus(proposal.getStatus());
                proposalView.setTime(proposal.getTime());
            } else {
                proposalView.setProposalId(-1);
                proposalView.setProposalStatus(-1);
                proposalView.setTime(-1);
            }

            proposalViewList.add(proposalView);
        }

        PageView<ProposalView> pageView = PageView.of(pair.getLeft(), proposalViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Get proposal detail information")
    @PostMapping(path = "/proposal/info", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<ProposalDetailView> getProposalById(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated IdQuery idQuery) {
        Optional<Proposal> mongoOption = proposalService.getProposalById(idQuery.getId());
        if (!mongoOption.isPresent()) {
            return ResultView.ok();
        }

        Proposal proposal = mongoOption.get();
        long nowSecond = System.currentTimeMillis() / 1000;
        if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal()) && proposal.getEndTime() < nowSecond) {
            proposal.setStatus(Proposal.Status.Rejected.ordinal());
        }

        double agreeBlocksSum = 0;
        double disagreeBlocksSum = 0;
        double allBlocksSum = 0;

        List<TodoList> todoList = todoService.listTodoByProposalId(proposal.getProposalId());
        List<String> todoAddressList = todoList.stream().map(TodoList::getOperator).collect(Collectors.toList());
        List<Account> todoUserList = daoAccountService.listAll(todoAddressList);
        Map<String, Account> todoUserMap = todoUserList.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<Vote> voteList = voteService.listVoteByProposalId(proposal.getProposalId());
        Map<String, Vote> voteMap = voteList.stream().collect(Collectors.toMap(Vote::getVoter, vote -> vote));

        List<Member> memberList = memberService.listMemberByRound(proposal.getRound());
        Map<String, Member> memberMap = memberList.stream().collect(Collectors.toMap(Member::getAddress, member -> member));

        Map<String, Object> args = CheckArgsUtil.generate(address, idQuery.getId());
        ResultView check = minerVoteCheck.check(args);

        List<VoterView> agreePerson = Lists.newArrayList();
        List<VoterView> disagreePerson = Lists.newArrayList();
        List<VoterView> notYetPerson = Lists.newArrayList();
        for (TodoList todo : todoList) {
            Account account = todoUserMap.get(todo.getOperator());
            VoterView voterView = VoterView.builder()
                    .address(todo.getOperator())
                    .name(account != null ? account.getName() : "")
                    .build();
            Vote vote = voteMap.get(todo.getOperator());
            Member member = memberMap.get(todo.getOperator());
            allBlocksSum += member.getProduced();
            if (vote != null) {
                voterView.setVoteTime(vote.getTime());
                if (vote.getDecision()) {
                    agreePerson.add(voterView);
                    agreeBlocksSum += member.getProduced();
                } else {
                    disagreePerson.add(voterView);
                    disagreeBlocksSum += member.getProduced();
                }
            } else {
                notYetPerson.add(voterView);
            }
        }

        Optional<TMinerOperation> mysqlOption = minerOperationService.getTMinerOperationByTxHash(proposal.getTxHash());
        String assetId = JSON.parseObject(mysqlOption.get().getAdditionalInfo()).getString(OperationAdditionalKey.ASSET);
        Optional<Asset> assetOptional = assetService.getAsset(assetId);

        String supportRate = "0.0";
        String rejectRate = "0.0";

        DecimalFormat df = new DecimalFormat("#0.00");
        if (allBlocksSum != 0) {
            supportRate = df.format(agreeBlocksSum / allBlocksSum * 100);
            rejectRate = df.format(disagreeBlocksSum / allBlocksSum * 100);
        }
        ProposalDetailView proposalDetailView = ProposalDetailView.builder()
                .txStatus(mysqlOption.get().getTxStatus())
                .proposalStatus(proposal.getStatus())
                .comment(JSON.parseObject(mysqlOption.get().getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT))
                .assetName(assetOptional.get().getName())
                .proposer(PersonView.builder()
                        .name(todoUserMap.get(proposal.getAddress()) != null ? todoUserMap.get(proposal.getAddress()).getName() : "")
                        .build())
                .time(proposal.getTime())
                .endTime(proposal.getEndTime())
                .type(proposal.getType())
                .proposalId(proposal.getProposalId())
                .agreeBlocksSum((long) allBlocksSum)
                .disagreeBlocksSum((long) allBlocksSum)
                .agreeVoters(agreePerson)
                .disagreeVoters(disagreePerson)
                .notYetVoters(notYetPerson)
                .passSupportRate(Condition.MINER_VOTE_DEFAULT_PASS_RATE)
                .effectiveHeight(proposal.getEffectiveHeight())
                .effectiveTime(proposal.getEffectiveTime())
                .allowVote(check.success())
                .supportRate(proposal.getSupportRate() != -1 ? String.valueOf(proposal.getSupportRate()) : supportRate)
                .rejectRate(proposal.getRejectRate() != -1 ? String.valueOf(proposal.getRejectRate()) : rejectRate)
                .build();
        return ResultView.ok(proposalDetailView);
    }

    @ApiOperation(value = "Check launch proposal")
    @PostMapping(path = "/committee/proposal/check")
    public ResultView checkSubmit(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated ProposalCheckRequest proposalCheckRequest) {
        Map<String, Object> args = CheckArgsUtil.generateProposal(address, proposalCheckRequest.getAsset().substring(2));
        return minerLaunchCheck.check(args);
    }

    @ApiOperation(value = "Launch proposal", notes = "call /miner/contract returns abi => startProposal(uint proposalType, uint assetType)")
    @PostMapping(path = "/committee/proposal/submit", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<TxView> submitProposal(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                             @RequestBody @Validated ProposalRequest proposalRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateProposal(address, proposalRequest.getAsset().substring(2));
        ResultView check = minerLaunchCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Round round = roundService.getCurrentRound();

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.ASSET, proposalRequest.getAsset().substring(2));
        additionalInfoMap.put(OperationAdditionalKey.COMMENT, proposalRequest.getComment());
        Map<String, Object> args = SendTxArgsUtil.generate(round.getRound(), MinerOperationType.Proposal.getCode(), additionalInfoMap, address);
        return minerSendDeletableTransaction.perform(proposalRequest.getCallData(), args);
    }
}
