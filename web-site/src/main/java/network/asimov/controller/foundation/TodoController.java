package network.asimov.controller.foundation;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mongodb.service.foundation.ProposalService;
import network.asimov.mongodb.service.foundation.TodoService;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.foundation.TodoListQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.foundation.TodoView;
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
 * @date 2019-10-15
 */
@CrossOrigin
@Api(tags = "foundation")
@RestController("foundationTodoController")
@RequestMapping(path = "/foundation", produces = RequestConstants.CONTENT_TYPE_JSON)
public class TodoController {
    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    @Resource(name = "foundationTodoService")
    private TodoService todoService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @ApiOperation(value = "Query my todo list by page")
    @PostMapping(path = "/todo/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<TodoView>> listTodo(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated TodoListQuery todoListQuery) {
        Pair<Long, List<TodoList>> pair = todoService.listTodoByAddress(todoListQuery.getProposalType(), todoListQuery.getPage().getIndex(), todoListQuery.getPage().getLimit(), address);

        List<Long> proposalIds = pair.getRight().stream().map(TodoList::getTodoId).collect(Collectors.toList());

        List<Proposal> proposalList = proposalService.listProposalByIds(proposalIds);
        Map<Long, Proposal> proposalMap = proposalList.stream().collect(Collectors.toMap(Proposal::getProposalId, proposal -> proposal));

        List<Account> list = daoAccountService.listAll(proposalList.stream().map(Proposal::getAddress).distinct().collect(Collectors.toList()));
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<TodoView> todoViewList = Lists.newArrayList();
        long nowSecond = System.currentTimeMillis() / 1000;
        long total = pair.getLeft();
        for (TodoList todo : pair.getRight()) {
            if (!proposalMap.containsKey(todo.getTodoId())) {
                throw BusinessException.builder().message(String.format("proposal not exist, proposal_id: %d", todo.getTodoId())).errorCode(ErrorCode.DATA_ERROR).build();
            }

            Proposal proposal = proposalMap.get(todo.getTodoId());
            if (proposal.getEndTime() < nowSecond) {
                total--;
                continue;
            }

            Account account = userMap.get(proposal.getAddress());
            todoViewList.add(TodoView.builder()
                    .type(proposal.getProposalType())
                    .time(todo.getTime())
                    .txStatus(TxStatus.Success.ordinal())
                    .proposalStatus(proposal.getStatus())
                    .proposalId(proposal.getProposalId())
                    .proposer(PersonView.builder()
                            .address(proposal.getAddress())
                            .name(account != null ? account.getName() : "")
                            .icon(account != null ? account.getAvatar() : "")
                            .build())
                    .build());
        }
        PageView<TodoView> pageView = PageView.of(total, todoViewList);

        return ResultView.ok(pageView);
    }
}