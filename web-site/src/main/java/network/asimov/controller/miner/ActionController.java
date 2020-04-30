package network.asimov.controller.miner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.chainrpc.pojo.ActionDTO;
import network.asimov.chainrpc.service.contract.ValidatorCommitteeService;
import network.asimov.mongodb.service.miner.MemberService;
import network.asimov.mongodb.service.miner.TodoService;
import network.asimov.request.RequestConstants;
import network.asimov.response.ResultView;
import network.asimov.response.miner.MinerActionView;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2019-09-27
 */

@CrossOrigin
@RestController("minerActionController")
@Api(tags = "miner")
@RequestMapping(path = "/miner", produces = RequestConstants.CONTENT_TYPE_JSON)
public class ActionController {

    @Resource(name = "validatorCommitteeService")
    private ValidatorCommitteeService validatorCommitteeService;

    @Resource(name = "minerMemberService")
    private MemberService memberService;

    @Resource(name = "minerTodoService")
    private TodoService todoService;

    @ApiOperation(value = "Gets the events that the user can initiate")
    @PostMapping(path = "/action")
    public ResultView<MinerActionView> getAction(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        boolean isMember = memberService.exist(address);
        if (!isMember) {
            return ResultView.ok(MinerActionView.builder().build());
        }

        ActionDTO actionDTO = validatorCommitteeService.getMinerAction();
        MinerActionView minerActionView = MinerActionView.builder()
                .proposalAction(actionDTO.getProposal())
                .voteAction(actionDTO.getVote()).build();
        for (Integer action : actionDTO.getVote()) {
            long count = todoService.countTodo(address, action);
            minerActionView.getTodoCount().put("vote_" + action, count);
        }

        return ResultView.ok(minerActionView);
    }
}
