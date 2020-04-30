package network.asimov.controller.foundation;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.constant.ConditionMap;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mongodb.entity.foundation.Vote;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.foundation.ProposalService;
import network.asimov.mongodb.service.foundation.TodoService;
import network.asimov.mongodb.service.foundation.VoteService;
import network.asimov.mysql.constant.FoundationOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.foundation.FoundationOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.IdQuery;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.foundation.LaunchCheckRequest;
import network.asimov.request.foundation.LaunchRequest;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.TxView;
import network.asimov.response.common.VoterView;
import network.asimov.response.foundation.ProposalDetailView;
import network.asimov.response.foundation.ProposalView;
import network.asimov.util.AmountUtil;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@CrossOrigin
@RestController("foundationProposalController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation/proposal", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ProposalController {
    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "foundationSendDeletableTransaction")
    private SendTransactionBehavior sendDeletableTransaction;

    @Resource(name = "foundationTodoService")
    private TodoService todoService;

    @Resource(name = "foundationVoteService")
    private VoteService voteService;

    @Resource(name = "foundationVoteCheck")
    private CheckBehavior foundationVoteCheck;

    @Resource(name = "foundationLaunchProposalCheck")
    private CheckBehavior launchCheck;

    @Resource(name = "assetService")
    private AssetService assetService;

    @ApiOperation(value = "Query my proposal by page")
    @PostMapping(path = "/mine", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ProposalView>> listMyProposal(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated PurePageQuery purePageQuery) {
        List<ProposalView> proposalViewList = Lists.newArrayList();
        Pair<Long, List<TFoundationOperation>> pair = foundationOperationService.listFoundationOperationByAddressAndType(address, FoundationOperationType.Proposal.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());

        Map<String, Proposal> proposalMap = proposalService.mapProposalByTxHash(pair.getRight().stream().map(TFoundationOperation::getTxHash).distinct().collect(Collectors.toList()));

        long nowSecond = TimeUtil.currentSeconds();
        for (TFoundationOperation operation : pair.getRight()) {
            ProposalView proposalView = ProposalView.builder()
                    .comment(JSON.parseObject(operation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT))
                    .txStatus(operation.getTxStatus())
                    .build();
            Proposal proposal = proposalMap.get(operation.getTxHash());
            if (proposal != null && proposal.getEndTime() < nowSecond) {
                if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                    proposal.setStatus(Proposal.Status.Rejected.ordinal());
                }
            }

            if (proposal != null) {
                proposalView.setProposalId(proposal.getProposalId());
                proposalView.setProposalStatus(proposal.getStatus());
                proposalView.setTime(operation.getCreateTime());
            } else {
                proposalView.setProposalId(-1);
                proposalView.setProposalStatus(-1);
                proposalView.setTime(operation.getCreateTime());
            }

            proposalViewList.add(proposalView);
        }

        PageView<ProposalView> pageView = PageView.of(pair.getLeft(), proposalViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Query proposal history by page")
    @PostMapping(path = "/history", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ProposalView>> listHistoryProposal(@RequestBody @Validated PurePageQuery purePageQuery) {
        List<ProposalView> proposalViewList = Lists.newArrayList();
        Pair<Long, List<TFoundationOperation>> pair = foundationOperationService.listFoundationOperationByType(FoundationOperationType.Proposal.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<String> hashList = pair.getRight().stream().map(TFoundationOperation::getTxHash).collect(Collectors.toList());

        Map<String, Proposal> proposalMap = proposalService.mapProposalByTxHash(hashList);

        List<Account> list = daoAccountService.listAll(pair.getRight().stream().map(TFoundationOperation::getOperator).distinct().collect(Collectors.toList()));
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        long nowSecond = System.currentTimeMillis() / 1000;
        for (TFoundationOperation operation : pair.getRight()) {
            Account account = userMap.get(operation.getOperator());
            Proposal proposal = proposalMap.get(operation.getTxHash());

            if (proposal != null && proposal.getEndTime() < nowSecond) {
                if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                    proposal.setStatus(Proposal.Status.Rejected.ordinal());
                }
            }

            proposalViewList.add(ProposalView.builder()
                    .comment(JSON.parseObject(operation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT))
                    .proposer(PersonView.builder()
                            .name(account != null ? account.getName() : "")
                            .build())
                    .time(operation.getCreateTime())
                    .proposalStatus(proposal != null ? proposal.getStatus() : -1)
                    .txStatus(operation.getTxStatus())
                    .proposalId(proposal != null ? proposal.getProposalId() : -1)
                    .build());
        }
        PageView<ProposalView> pageView = PageView.of(pair.getLeft(), proposalViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Get proposal detail information")
    @PostMapping(path = "/info", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<ProposalDetailView> getProposalById(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated IdQuery idQuery) {
        Optional<Proposal> mongoOption = proposalService.getProposalById(idQuery.getId());
        if (!mongoOption.isPresent()) {
            return ResultView.ok();
        }

        Proposal proposal = mongoOption.get();
        long nowSecond = System.currentTimeMillis() / 1000;
        if (proposal.getEndTime() < nowSecond) {
            if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal())) {
                proposal.setStatus(Proposal.Status.Rejected.ordinal());
            }
        }

        Optional<TFoundationOperation> mysqlOption = foundationOperationService.getTFoundationOperationByTxHash(proposal.getTxHash());
        String additionalInfo = mysqlOption.get().getAdditionalInfo();

        String assetId = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.INVEST_ASSET);
        String investAmount = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.INVEST_AMOUNT);
        String targetAddress = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.TARGET_ADDRESS);

        List<TodoList> todoList = todoService.listTodoByProposalId(proposal.getProposalId());
        List<String> todoAddressList = todoList.stream().map(TodoList::getOperator).collect(Collectors.toList());

        todoAddressList.add(proposal.getAddress());
        todoAddressList.add(targetAddress);

        List<Account> todoUserList = daoAccountService.listAll(todoAddressList);
        Map<String, Account> todoUserMap = todoUserList.stream().distinct().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<Vote> voteList = voteService.listVoteByProposalId(proposal.getProposalId());
        Map<String, Vote> voteMap = voteList.stream().collect(Collectors.toMap(Vote::getVoter, vote -> vote));

        Map<String, Object> args = CheckArgsUtil.generate(address, idQuery.getId());
        ResultView check = foundationVoteCheck.check(args);

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

            if (vote != null) {
                voterView.setVoteTime(vote.getTime());
                if (vote.getDecision()) {
                    agreePerson.add(voterView);
                } else {
                    disagreePerson.add(voterView);
                }
            } else {
                notYetPerson.add(voterView);
            }
        }
        Map<Integer, Double> passRateMap = ConditionMap.getPassRateMap();

        Optional<Asset> asset = assetService.getAsset(assetId != null && assetId.startsWith("0x") ? assetId.substring(2) : assetId);
        ProposalDetailView proposalDetailView = ProposalDetailView.builder()
                .txStatus(mysqlOption.get().getTxStatus())
                .proposalStatus(proposal.getStatus())
                .comment(JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.COMMENT))
                .proposer(PersonView.builder()
                        .name(todoUserMap.get(proposal.getAddress()) != null ? todoUserMap.get(proposal.getAddress()).getName() : "")
                        .build())
                .time(proposal.getTime())
                .endTime(proposal.getEndTime())
                .type(proposal.getProposalType())
                .proposalId(proposal.getProposalId())
                .agreeVoters(agreePerson)
                .disagreeVoters(disagreePerson)
                .notYetVoters(notYetPerson)
                .passSupportRate(passRateMap.get(proposal.getProposalType()))
                .investAsset(AssetView.builder()
                        .asset(assetId)
                        .value(StringUtils.isNotEmpty(investAmount) ? AmountUtil.asimToXing(investAmount) : "0")
                        .name(asset.isPresent() ? asset.get().getName() : Strings.EMPTY)
                        .symbol(asset.isPresent() ? asset.get().getSymbol() : Strings.EMPTY)
                        .logo(asset.isPresent() ? asset.get().getLogo() : Strings.EMPTY)
                        .build())
                .targetPerson(PersonView.builder()
                        .address(targetAddress)
                        .name(todoUserMap.get(targetAddress) != null ? todoUserMap.get(targetAddress).getName() : "")
                        .build())
                .allowVote(check.success())
                .build();
        return ResultView.ok(proposalDetailView);
    }

    @ApiOperation(value = "Check launch proposal")
    @PostMapping(path = "/launch/check", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView checkLaunch(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                  @RequestBody @Validated LaunchCheckRequest launchCheckRequest) {
        Long amount = null;
        if (launchCheckRequest.getProposalType() == Proposal.Type.Expenses.ordinal()) {
            amount = launchCheckRequest.getInvestAmount();
        }

        Map<String, Object> checkArg = CheckArgsUtil.generateProposal(address, launchCheckRequest.getProposalType(), amount, launchCheckRequest.getTargetAddress());
        return launchCheck.check(checkArg);
    }

    @ApiOperation(value = "Launch proposal", notes = "call /foundation/contract returns abi => startProposal(uint proposalType, address targetAddress, uint amount, uint asset) ")
    @PostMapping(path = "/launch", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<TxView> launch(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                     @RequestBody @Valid LaunchRequest launchRequest) {
        Long amount = null;
        if (launchRequest.getProposalDetail().getProposalType() == Proposal.Type.Expenses.ordinal()) {
            amount = launchRequest.getProposalDetail().getInvestAmount();
        }
        Map<String, Object> checkArg = CheckArgsUtil.generateProposal(address, launchRequest.getProposalDetail().getProposalType(), amount, launchRequest.getProposalDetail().getTargetAddress());
        ResultView check = launchCheck.check(checkArg);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfo = this.getAdditionalInfoMap(launchRequest);
        Map<String, Object> args = SendTxArgsUtil.generate(FoundationOperationType.Proposal.getCode(), additionalInfo, address);

        return sendDeletableTransaction.perform(launchRequest.getCallData(), args);

    }

    private Map<String, Object> getAdditionalInfoMap(LaunchRequest launchRequest) {
        Map<String, Object> additionalInfo = new HashMap<>(4);
        Integer proposalType = launchRequest.getProposalDetail().getProposalType();
        additionalInfo.put(OperationAdditionalKey.PROPOSAL_TYPE, launchRequest.getProposalDetail().getProposalType());
        additionalInfo.put(OperationAdditionalKey.COMMENT, launchRequest.getProposalDetail().getComment());
        additionalInfo.put(OperationAdditionalKey.TARGET_ADDRESS, launchRequest.getProposalDetail().getTargetAddress());

        // 投资费用
        if (Proposal.Type.Expenses.ordinal() == proposalType) {
            additionalInfo.put(OperationAdditionalKey.INVEST_AMOUNT, launchRequest.getProposalDetail().getInvestAmount());
            additionalInfo.put(OperationAdditionalKey.INVEST_ASSET, launchRequest.getProposalDetail().getInvestAsset());
        }

        return additionalInfo;
    }
}

