package network.asimov.controller.dorg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.entity.dorg.TodoList;
import network.asimov.mongodb.entity.dorg.Vote;
import network.asimov.mongodb.service.dorg.ProposalService;
import network.asimov.mongodb.service.dorg.TodoListService;
import network.asimov.mongodb.service.dorg.VoteService;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.database.tables.pojos.TDaoIndivisibleAsset;
import network.asimov.mysql.database.tables.pojos.TDaoOperation;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.dorg.DaoIndivisibleAssetService;
import network.asimov.mysql.service.dorg.DaoOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.ContractPageQuery;
import network.asimov.request.dorg.DaoProposalIdQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.AssetView;
import network.asimov.response.common.PersonView;
import network.asimov.response.dorg.ProposalDetailView;
import network.asimov.response.dorg.TaskView;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-12-11
 */
@CrossOrigin
@RestController("daoTaskController")
@Api(tags = "dao")
@RequestMapping(path = "/dao/org/task", produces = RequestConstants.CONTENT_TYPE_JSON)
public class TaskController {
    @Resource(name = "daoTodoListService")
    private TodoListService todoListService;

    @Resource(name = "daoProposalService")
    private ProposalService proposalService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "daoVoteService")
    private VoteService voteService;

    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @Resource(name = "daoVoteCheck")
    private CheckBehavior daoVoteCheck;

    @Resource(name = "daoIndivisibleAssetService")
    private DaoIndivisibleAssetService daoIndivisibleAssetService;

    @ApiOperation(value = "List my todo vote task")
    @PostMapping("/list/tovote")
    public ResultView<PageView<TaskView>> toVoteTask(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                     @Validated @RequestBody ContractPageQuery contractPageQuery) {
        Integer todoType = TodoList.Type.Vote.ordinal();
        boolean operated = false;
        return listTaskViewByTodoType(contractPageQuery.getContractAddress(), address, todoType, operated, contractPageQuery.getPage().getIndex(), contractPageQuery.getPage().getLimit());
    }

    @ApiOperation(value = "List voted task")
    @PostMapping("/list/voted")
    public ResultView votedTask(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @Validated @RequestBody ContractPageQuery contractPageQuery) {
        Integer todoType = TodoList.Type.Vote.ordinal();
        boolean operated = true;
        return listTaskViewByTodoType(contractPageQuery.getContractAddress(), address, todoType, operated, contractPageQuery.getPage().getIndex(), contractPageQuery.getPage().getLimit());
    }

    private ResultView<PageView<TaskView>> listTaskViewByTodoType(String contractAddress, String address, Integer todoType, boolean operated, Integer index, Integer limit) {
        List<TaskView> taskViewList = Lists.newArrayList();
        Pair<Long, List<TodoList>> pair = todoListService.listMyTodoList(contractAddress, address, todoType, operated, index, limit);

        long totalCount = pair.getLeft();

        List<Long> proposalIds = pair.getRight().stream().map(TodoList::getTodoId).collect(Collectors.toList());
        List<Proposal> proposalList = proposalService.listProposalByIds(contractAddress, proposalIds);
        Map<Long, Proposal> proposalMap = proposalList.stream().collect(Collectors.toMap(Proposal::getProposalId, proposal -> proposal));
        for (TodoList todo : pair.getRight()) {
            Proposal proposal = proposalMap.get(todo.getTodoId());
            if (operated == false && (!proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal()) || proposal.getEndTime() < TimeUtil.currentSeconds())) {
                totalCount--;
                continue;
            }
            TDaoAccount account = daoAccountService.findByAddress(proposal.getAddress());
            taskViewList.add(TaskView.builder()
                    .proposalId(proposal.getProposalId())
                    .proposer(PersonView.builder()
                            .address(proposal.getAddress())
                            .name(account != null ? account.getNickName() : "")
                            .icon(account != null ? account.getAvatar() : "")
                            .build())
                    .createTime(proposal.getTime())
                    .proposalType(proposal.getProposalType())
                    .build());
        }

        return ResultView.ok(PageView.of(totalCount, taskViewList));
    }

    @PostMapping("/vote/query")
    @ApiOperation(value = "Get vote detail information")
    public ResultView<ProposalDetailView> queryVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                    @Validated @RequestBody DaoProposalIdQuery daoProposalIdQuery) {
        Optional<Proposal> mongoOption = proposalService.getProposalById(daoProposalIdQuery.getId(), daoProposalIdQuery.getContractAddress());
        if (!mongoOption.isPresent()) {
            return ResultView.ok();
        }
        Proposal proposal = mongoOption.get();
        if (proposal.getStatus().equals(Proposal.Status.OnGoing.ordinal()) && proposal.getEndTime() < TimeUtil.currentSeconds()) {
            proposal.setStatus(Proposal.Status.Expired.ordinal());
        }

        Optional<TDaoOperation> mysqlOption = daoOperationService.getOperationByTxHash(proposal.getTxHash());
        String additionalInfo = mysqlOption.get().getAdditionalInfo();
        String asset = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.ASSET);
        String assetName = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.ASSET_NAME);
        String assetSymbol = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.ASSET_SYMBOL);
        String assetLogo = JSON.parseObject(additionalInfo).getString(OperationAdditionalKey.ASSET_LOGO);
        Long issueAmount = JSON.parseObject(additionalInfo).getLong(OperationAdditionalKey.ISSUE_AMOUNT);
        List<TodoList> todoList = todoListService.listTodoByProposalId(proposal.getProposalId(), daoProposalIdQuery.getContractAddress());

        List<Vote> voteList = voteService.listVoteByProposalId(proposal.getProposalId(), daoProposalIdQuery.getContractAddress());

        int agreeVotersCount = 0;
        int disagreeVotersCount = 0;
        Boolean myDecision = null;

        for (Vote vote : voteList) {
            if (vote.getVoter().equals(address)) {
                myDecision = vote.getDecision();
            }
            if (vote.getDecision()) {
                agreeVotersCount++;
            } else {
                disagreeVotersCount++;
            }
        }

        TDaoAccount account = daoAccountService.findByAddress(proposal.getAddress());

        Map<String, Object> args = CheckArgsUtil.generateVote(address, daoProposalIdQuery.getContractAddress(), daoProposalIdQuery.getId());
        ResultView check = daoVoteCheck.check(args);

        List<AssetView.Indivisible> indivisibleList = new ArrayList<>();
        List<TDaoIndivisibleAsset> indivisibleAssetList = daoIndivisibleAssetService.listIndivisibleAssets(asset);
        for (TDaoIndivisibleAsset indivisibleAsset : indivisibleAssetList) {
            indivisibleList.add(AssetView.Indivisible.builder()
                    .number(String.valueOf(indivisibleAsset.getVoucherId()))
                    .description(indivisibleAsset.getAssetDesc())
                    .build());
        }

        ProposalDetailView proposalDetailView = ProposalDetailView.builder()
                .proposalId(proposal.getProposalId())
                .proposalStatus(proposal.getStatus())
                .proposer(PersonView.builder()
                        .address(proposal.getAddress())
                        .name(account != null ? account.getNickName() : "")
                        .icon(account != null ? account.getAvatar() : "")
                        .build())
                .txStatus(mysqlOption.get().getTxStatus())
                .agreeVotersCount(agreeVotersCount)
                .disagreeVotersCount(disagreeVotersCount)
                .totalVotersCount(todoList.size())
                .createTime(proposal.getTime())
                .endTime(proposal.getEndTime())
                .issueAsset(AssetView.builder()
                        .asset(asset)
                        .name(assetName)
                        .symbol(assetSymbol)
                        .logo(assetLogo)
                        .value(String.valueOf(issueAmount))
                        .indivisibleList(indivisibleList)
                        .build())
                .myDecision(myDecision)
                .passSupportRate(67D)
                .allowVote(check.success())
                .proposalType(proposal.getProposalType())
                .build();

        return ResultView.ok(proposalDetailView);
    }
}
