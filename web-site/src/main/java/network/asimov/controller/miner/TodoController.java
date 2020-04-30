package network.asimov.controller.miner;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.TodoList;
import network.asimov.mongodb.service.miner.ProposalService;
import network.asimov.mongodb.service.miner.TodoService;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.miner.ProposalView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-24
 */
@CrossOrigin
@RestController("minerTodoController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON, consumes = RequestConstants.CONTENT_TYPE_JSON)
public class TodoController {
    @Resource(name = "minerTodoService")
    private TodoService todoService;

    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @ApiOperation(value = "Query my todo list by page")
    @PostMapping(path = "/todo/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<ProposalView>> listTodo(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<TodoList>> pair = todoService.listTodoByAddress(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit(), address);

        List<Long> proposalIds = pair.getRight().stream().map(TodoList::getActionId).collect(Collectors.toList());

        List<Proposal> proposalList = proposalService.listProposalByIds(proposalIds);
        Map<Long, Proposal> proposalMap = proposalList.stream().collect(Collectors.toMap(Proposal::getProposalId, proposal -> proposal));

        List<Account> list = daoAccountService.listAll(proposalList.stream().map(Proposal::getAddress).distinct().collect(Collectors.toList()));
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<ProposalView> proposalViewList = Lists.newArrayList();
        long nowSecond = System.currentTimeMillis() / 1000;
        long total = pair.getLeft();
        for (TodoList todoList : pair.getRight()) {
            if (!proposalMap.containsKey(todoList.getActionId())) {
                throw BusinessException.builder().message(String.format("proposal not exist, proposal_id: %d", todoList.getActionId())).errorCode(ErrorCode.DATA_ERROR).build();
            }

            Proposal proposal = proposalMap.get(todoList.getActionId());
            if (proposal.getEndTime() < nowSecond) {
                total--;
                continue;
            }

            Account account = userMap.get(proposal.getAddress());
            proposalViewList.add(ProposalView.builder()
                    .type(proposal.getType())
                    .time(todoList.getTime())
                    .proposalStatus(proposal.getStatus())
                    .proposalId(proposal.getProposalId())
                    .proposer(PersonView.builder()
                            .address(proposal.getAddress())
                            .name(account != null ? account.getName() : "")
                            .icon(account != null ? account.getAvatar() : "")
                            .build())
                    .build());
        }
        PageView<ProposalView> pageView = PageView.of(total, proposalViewList);

        return ResultView.ok(pageView);
    }
}
