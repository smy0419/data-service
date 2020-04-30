package network.asimov.controller.miner;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.mongodb.entity.miner.Member;
import network.asimov.mongodb.entity.miner.SignUp;
import network.asimov.mongodb.service.miner.MemberService;
import network.asimov.mongodb.service.miner.ProposalService;
import network.asimov.mongodb.service.miner.SignUpService;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.miner.MemberFlagView;
import network.asimov.response.miner.MinerView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-27
 */
@CrossOrigin
@RestController("minerMemberController")
@Api(tags = "miner")
@RequestMapping(path = "/miner/member", produces = RequestConstants.CONTENT_TYPE_JSON)
public class MemberController {
    @Resource(name = "minerMemberService")
    private MemberService memberService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "minerProposalService")
    private ProposalService proposalService;

    @Resource(name = "minerSignUpService")
    private SignUpService signUpService;


    @ApiOperation(value = "Query miner member by page")
    @PostMapping(path = "/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<MinerView>> listMember(@RequestBody @Validated PurePageQuery purePageQuery) {
        List<MinerView> memberViewList = Lists.newArrayList();
        Pair<Long, List<Member>> pair = memberService.listCurrentMiningMember(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<String> addressList = pair.getRight().stream().map(Member::getAddress).distinct().collect(Collectors.toList());
        List<Account> list = daoAccountService.listAll(addressList);
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));
        Map<String, Long> proposalCountMap = proposalService.getProposalCountMap();

        for (Member member : pair.getRight()) {
            Account account = userMap.get(member.getAddress());
            String address = member.getAddress();
            memberViewList.add(MinerView.builder()
                    .member(PersonView.builder()
                            .address(member.getAddress())
                            .name(account != null ? account.getName() : "")
                            .build())
                    .produced(member.getProduced())
                    .efficiency(member.getEfficiency())
                    .proposalCount(proposalCountMap.containsKey(address) ? proposalCountMap.get(address) : 0L)
                    .build());
        }

        PageView pageView = PageView.of(pair.getLeft(), memberViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Get miner member's flag")
    @PostMapping(path = "/flag")
    public ResultView<MemberFlagView> getMemberFlag(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        MemberFlagView memberFlagView = MemberFlagView.builder().build();
        Optional<Member> optionalMember = memberService.getCurrentMiningMember(address);
        if (optionalMember.isPresent()) {
            memberFlagView.setMember(true);
        } else {
            memberFlagView.setMember(false);
        }
        return ResultView.ok(memberFlagView);
    }

    @ApiOperation(value = "Query ranking list by page")
    @PostMapping(path = "/ranking/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<MinerView>> listRanking(@RequestBody @Validated PurePageQuery purePageQuery) {
        List<MinerView> memberViewList = Lists.newArrayList();
        Pair<Long, List<SignUp>> pair = signUpService.listNextRoundSignUp(purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());

        List<Account> list = daoAccountService.listAll(pair.getRight().stream().map(SignUp::getAddress).distinct().collect(Collectors.toList()));
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        for (SignUp signUp : pair.getRight()) {
            memberViewList.add(MinerView.builder()
                    .member(PersonView.builder()
                            .name(userMap.get(signUp.getAddress()) != null ? userMap.get(signUp.getAddress()).getName() : "")
                            .build())
                    .efficiency(signUp.getEfficiency())
                    .produced(signUp.getProduced())
                    .build());
        }
        PageView<MinerView> pageView = PageView.of(pair.getLeft(), memberViewList);

        return ResultView.ok(pageView);
    }
}
