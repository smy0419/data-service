package network.asimov.controller.foundation;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.constant.FoundationAction;
import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mongodb.service.foundation.MemberService;
import network.asimov.mongodb.service.foundation.TodoService;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.foundation.ActionView;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-10-24
 */
@CrossOrigin
@RestController("foundationActionController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ActionController {
    @Resource(name = "foundationMemberService")
    private MemberService memberService;

    @Resource(name = "foundationTodoService")
    private TodoService todoService;

    @ApiOperation(value = "Gets the events that the user can initiate")
    @PostMapping(path = "/action")
    public ResultView<ActionView> getAction(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        Optional<Member> memberOptional = memberService.findInServiceMember(address);
        if (!memberOptional.isPresent()) {
            return ResultView.ok(ActionView.builder().build());
        }

        List<Integer> foundationAction = Lists.newArrayList(FoundationAction.Elect.ordinal(), FoundationAction.Impeach.ordinal(), FoundationAction.Expenses.ordinal());

        ActionView actionView = ActionView.builder()
                .proposalAction(foundationAction)
                .voteAction(foundationAction)
                .build();
        for (Integer type : foundationAction) {
            List<TodoList> todoList = todoService.listTodoByAddressAndProposalType(address, type);
            actionView.getTodoCount().put("vote_" + type, (long) todoList.size());
        }

        return ResultView.ok(actionView);
    }
}
